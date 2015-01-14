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
package s_mach.aeondb.diffmap

import org.joda.time.Instant
import s_mach.aeondb._
import s_mach.datadiff.DataDiff

import scala.concurrent.Future

object DiffMap {
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
}

// Note: B/PB must be invariant here b/c of DataDiff type-class
trait DiffMap[A,B,PB] extends AeonMap[A,B] {

  implicit def dataDiff:DataDiff[B,PB]

  trait OldMoment extends super.OldMoment {
    override def filterKeys(f: (A) => Boolean): OldMoment

    def checkout() : Future[DiffMap[A,B,PB]]
  }

  trait NowMoment extends super.NowMoment with OldMoment {
    override def filterKeys(f: (A) => Boolean): NowMoment

    def commit(
      checkout: Checkout[A],
      oomCommit: List[(Commit[A,B,PB],Metadata)]
    ) : Future[Boolean]

    def commitFold[X](
      f: Moment[A,B] => Future[(Checkout[A],List[(Commit[A,B,PB],Metadata)],X)],
      g: Exception => X
    ) : Future[X]

    def merge(
      other: DiffMap[A,B,PB]
    )(implicit metadata: Metadata) : Future[Boolean]

    def mergeFold[X](
      f: Moment[A,B] => Future[(DiffMap[A,B,PB],X)],
      g: Exception => X
    )(implicit metadata: Metadata) : Future[X]
  }

  override def base: OldMoment
  override def old(when: Instant) : OldMoment
  override def now : NowMoment

  def zomCommit: Future[List[(Commit[A,B,PB], Metadata)]]

  protected def emitEvents : Boolean = false
  protected def onEvent(e: DiffMap.Event[A,B,PB]) : Unit = { }
}
