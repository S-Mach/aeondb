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

import scala.concurrent.Future

trait AeonMap[A,B] {

  trait OldMoment extends Moment[A,B] {
    override def filterKeys(f: (A) => Boolean): OldMoment

    def aeon: Aeon
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
}