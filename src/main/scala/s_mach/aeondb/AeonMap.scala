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
import s_mach.aeondb.impl.{EventPublishing, AeonMapImpl}
import s_mach.datadiff.DataDiff
import scala.concurrent.{ExecutionContext, Future}
import s_mach.aeondb.internal._

object AeonMap {
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

//  def apply[A,B,PB](
//    base: MaterializedMoment[A,B]
//  )(implicit
//    dataDiff:DataDiff[B,PB],
//    ec:ExecutionContext
//  ) : AeonMap[A,B,PB] = {
//    new AeonMapImpl[A,B,PB](
//      _baseState = base
//    )
//  }
//
//  def apply[A,B,PB](
//    oomSubscriber: EventHandler[A,B,PB]*
//  )(
//    base: MaterializedMoment[A,B]
//  )(implicit
//    dataDiff:DataDiff[B,PB],
//    ec:ExecutionContext
//  ) : AeonMap[A,B,PB] = {
//    val _oomSubscriber = oomSubscriber
//    new AeonMapImpl[A,B,PB](
//      _baseState = base
//    ) with EventPublishing[A,B,PB] {
//      override def oomSubscriber = _oomSubscriber
//    }
//  }
}

trait AeonMap[A,B,PB] {
  implicit def executionContext: ExecutionContext
  implicit def dataDiff: DataDiff[B,PB]

  val NoOldMoment : OldMoment[A,B,PB]

  def base : OldMoment[A,B,PB]
  def old(when: Instant) : OldMoment[A,B,PB]
  def now : NowMoment[A,B,PB]
  def future(
    f: FutureMoment[A,B,PB] => Future[FutureMoment[A,B,PB]]
  )(implicit metadata:Metadata) : Future[Boolean]

  def zomCommit: Future[List[(Commit[A,B,PB], Metadata)]]

  protected def unsafeOnCommitHook(oomCommit: List[(Commit[A,B,PB],Metadata)]) : Unit = { }
}