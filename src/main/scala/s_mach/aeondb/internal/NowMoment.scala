package s_mach.aeondb.internal

import scala.concurrent.Future
import s_mach.aeondb._

trait NowMoment[A,B,PB] extends OldMoment[A,B,PB] {
  override def filterKeys(f: (A) => Boolean): NowMoment[A,B,PB]

  def deactivate(key: A)(implicit metadata: Metadata) : Future[Boolean]
  def reactivate(key: A, value: B)(implicit metadata: Metadata) : Future[Boolean]

  def put(key: A, value: B)(implicit metadata:Metadata) : Future[Boolean]

  def putFold[X](key: A)(
    f: Moment[A,B] => Future[(B,X)],
    g: Exception => X
  )(implicit metadata:Metadata) : Future[X]

  def replace(
    key: A,
    value: B
  )(implicit metadata:Metadata) : Future[Boolean]

  def replaceFold[X](key: A)(
    f: Moment[A,B] => Future[(B,X)],
    g: Exception => X
  )(implicit metadata:Metadata) : Future[X]

  // def append[C](key: A, value: C)(implicit monoid: Monoid[B[C]])
  // def put(value: B)(implicit uuidGen:UUIDGenerator[A])

  def commit(
    checkout: Checkout[A],
    oomCommit: List[(Commit[A,B,PB],Metadata)]
  ) : Future[Boolean]

  def commitFold[X](
    f: Moment[A,B] => Future[(Checkout[A],List[(Commit[A,B,PB],Metadata)],X)],
    g: Exception => X
  ) : Future[X]

  def merge(
    other: AeonMap[A,B,PB]
  )(implicit metadata: Metadata) : Future[Boolean]

  def mergeFold[X](
    f: Moment[A,B] => Future[(AeonMap[A,B,PB],X)],
    g: Exception => X
  )(implicit metadata: Metadata) : Future[X]
}

