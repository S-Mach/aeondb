/*
                    ,i::,
               :;;;;;;;
              ;:,,::;.
            1ft1;::;1tL
              t1;::;1,
               :;::;               _____       __  ___              __
          fCLff ;:: tfLLC         / ___/      /  |/  /____ _ _____ / /_
         CLft11 :,, i1tffLi       \__ \ ____ / /|_/ // __ `// ___// __ \
         1t1i   .;;   .1tf       ___/ //___// /  / // /_/ // /__ / / / /
       CLt1i    :,:    .1tfL.   /____/     /_/  /_/ \__,_/ \___//_/ /_/
       Lft1,:;:       , 1tfL:
       ;it1i ,,,:::;;;::1tti      AeonDB
         .t1i .,::;;; ;1tt        Copyright (c) 2014 S-Mach, Inc.
         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
          .L1 1tt1ttt,,Li
            ...1LLLL...
*/
package s_mach.aeondb

import org.joda.time.Instant
import s_mach.aeondb.impl.AeonMapImpl
import s_mach.datadiff.DataDiff

import scala.concurrent.{ExecutionContext, Future}

object AeonMap {
  sealed trait Event[A,B,PB]
  case class OnCommit[A,B,PB](
    oomCommit: List[(Commit[A,B,PB],Metadata)]
  ) extends Event[A,B,PB]

  sealed trait Error[+A] extends Exception
  case class KeyNotFoundError[+A](oomKeyNotFound: Iterable[A]) extends Error[A]
  case class EmptyCommitError() extends Error[Nothing]

  case class VersionMismatch[+A](key: A, expectedVersion: Long, version: Long)

  sealed trait MergeError[+A] extends Error[A]
  case class MergeConflictError[+A](oomMergeConflict: Iterable[VersionMismatch[A]]) extends MergeError[A]

  sealed trait CommitError[+A] extends Error[A]

  sealed trait PutError[+A] extends Error[A]
  case class KeyAlreadyExists[+A](key: A) extends PutError[A]

  type EventHandler[A,B,PB] = Seq[Event[A,B,PB]] => Unit

  def apply[A,B,PB](
    kv: (A,B)*
  )(implicit
    dataDiff:DataDiff[B,PB],
    ec:ExecutionContext
  ) : AeonMap[A,B,PB] =
    new AeonMapImpl(_baseState = MaterializedMoment(kv:_*))

  def apply[A,B,PB](
    oomSubscriber: EventHandler[A,B,PB]*
  )(
    kv: (A,B)*
  )(implicit
    dataDiff:DataDiff[B,PB],
    ec:ExecutionContext
  ) : AeonMap[A,B,PB] = {
    val _oomSubscriber = oomSubscriber
    new AeonMapImpl[A,B,PB](
      _baseState = MaterializedMoment(kv:_*)
    ) with EventPublishing[A,B,PB] {
      override def oomSubscriber = _oomSubscriber
    }
  }
}

trait AeonMap[A,B,PB] {
  import AeonMap._

  implicit def dataDiff:DataDiff[B,PB]

  trait OldMoment extends Moment[A,B] {
    override def filterKeys(f: (A) => Boolean): OldMoment

    def aeon: Aeon

    def checkout() : Future[AeonMap[A,B,PB]]

//    def oomCommit: Future[List[(Commit[A,B,PB],Metadata)]]
  }

  trait NowMoment extends OldMoment {
    override def filterKeys(f: (A) => Boolean): NowMoment

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

  trait FutureMoment {
    def find(key: A) : Future[Option[B]]

    def deactivate(key: A) : FutureMoment
    def reactivate(key: A, value: B) : FutureMoment

    def put(key: A, value: B) : FutureMoment
    def replace(key: A, value: B) : FutureMoment
  }

  val NoOldMoment : OldMoment

  def base : OldMoment
  def old(when: Instant) : OldMoment
  def now : NowMoment
  def future(
    f: FutureMoment => Future[FutureMoment]
  )(implicit metadata:Metadata) : Future[Boolean]

  def zomCommit: Future[List[(Commit[A,B,PB], Metadata)]]

  protected def unsafeOnCommitHook(oomCommit: List[(Commit[A,B,PB],Metadata)]) : Unit = { }
}