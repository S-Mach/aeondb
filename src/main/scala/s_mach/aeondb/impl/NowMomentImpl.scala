package s_mach.aeondb.impl

import scala.concurrent.Future
import s_mach.concurrent._
import s_mach.datadiff._
import s_mach.aeondb._
import s_mach.aeondb.internal._

case class NowMomentImpl[A,B,PB](
  aeonMap: AeonMapImpl[A,B,PB],
  oldMoment: OldMomentImpl[A,B,PB]
) extends NowMoment[A,B,PB] with LiftedLocalMoment[A,B,LocalMoment[A,B]] {
  import aeonMap._

  def aeon = oldMoment.aeon
  def local = oldMoment.local

  override def filterKeys(f: (A) => Boolean): NowMomentImpl[A,B,PB] =
    copy(oldMoment = oldMoment.filterKeys(f))

  override def put(
    key: A,
    value: B
  )(implicit metadata:Metadata) : Future[Boolean] = {
    putFold(key)(
      f = { _ => (value,true).future },
      g = { _ => false }
    )
  }

  override def putFold[X](key: A)(
    f: Moment[A,B] => Future[(B,X)],
    g: Exception => X
  )(implicit metadata:Metadata) : Future[X] = {
    aeonMap._commitFold({ nowMoment =>
      if(
        nowMoment.local.active.contains(key) == false &&
        nowMoment.local.inactive.contains(key) == false
      ) {
        for {
          (value,x) <- f(nowMoment)
        } yield {
          val (checkout,commit) = CommitBuilder[A,B,PB]()
            .put(key,value)
            .result()
          (checkout,(commit,metadata) :: Nil,x)
        }
      } else {
        (Checkout.empty[A,Long],Nil,
          g(KeyAlreadyExists(key))
        ).future
      }
    },g)
  }


  override def replace(
    key: A,
    value: B
  )(implicit metadata:Metadata) : Future[Boolean] = {
    replaceFold(key)(
      f = { _ => (value,true).future },
      g = { _ => false }
    )
  }

  override def replaceFold[X](key: A)(
    f: Moment[A,B] => Future[(B,X)],
    g: Exception => X
  )(implicit metadata:Metadata) : Future[X] = {
    aeonMap._commitFold({ nowMoment =>
      if(
        nowMoment.local.active.contains(key) ||
        nowMoment.local.inactive.contains(key)
      ) {
        val record = nowMoment.local.active(key)
        val oldValue = record.value
        for {
          (newValue, x) <- f(nowMoment)
        } yield {
          val patch = oldValue calcDiff newValue
          val (checkout,commit) = CommitBuilder[A,B,PB]()
            .replace(key,patch,record.version)
            .result()

          (checkout, (commit,metadata) :: Nil, x)
        }
      } else {
        (Checkout.empty[A,Long],Nil,
          g(KeyNotFoundError(Iterable(key)))
        ).future
      }
    },g)
  }

  override def deactivate(
    key: A
  )(implicit metadata: Metadata) : Future[Boolean] = {
    aeonMap._commitFold({ nowMoment =>
      nowMoment.local.active.get(key) match {
        case Some(record) =>
          val (checkout,commit) = CommitBuilder[A,B,PB]()
            .deactivate(key,record.version)
            .result()
          (checkout, (commit,metadata) :: Nil, true).future
        case None =>
          (Checkout.empty[A,Long],Nil,false).future
      }
    },{ _ => false })
  }

  override def reactivate(
    key: A,
    value: B
  )(implicit metadata: Metadata) : Future[Boolean] = {
    aeonMap._commitFold({ nowMoment =>
      nowMoment.local.inactive.get(key) match {
        case Some(record) =>
          val (checkout,commit) = CommitBuilder[A,B,PB]()
            .reactivate(key,value,record.version)
            .result()
          (checkout, (commit,metadata) :: Nil, true).future
        case None =>
          (Checkout.empty[A,Long],Nil, false).future
      }
    }, { _ => false })
  }

  override def commit(
    checkout: Checkout[A],
    oomCommit: List[(Commit[A,B,PB],Metadata)]
  ) : Future[Boolean] = {
    commitFold(
      f = { _ => (checkout,oomCommit,true).future },
      g = { _ => false }
    )
  }

  override def commitFold[X](
    f: Moment[A,B] => Future[(Checkout[A],List[(Commit[A,B,PB],Metadata)],X)],
    g: Exception => X
  ) : Future[X] = {
    aeonMap._commitFold(f,g)
  }


  override def merge(
    other: AeonMap[A,B,PB]
  )(implicit metadata: Metadata) : Future[Boolean] = {
    mergeFold(
      f = { _ => (other,true).future },
      g = { _ => false }
    )
  }

  override def mergeFold[X](
    f: Moment[A,B] => Future[(AeonMap[A,B,PB],X)],
    g: Exception => X
  )(implicit metadata: Metadata) : Future[X] = {
    aeonMap._mergeFold(f,g)
  }

  override def checkout(): Future[AeonMapImpl[A,B,PB]] = oldMoment.checkout()
}

