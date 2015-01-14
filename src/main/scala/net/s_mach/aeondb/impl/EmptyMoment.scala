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
package net.s_mach.aeondb.impl

import net.s_mach.aeondb._
import s_mach.concurrent._

import scala.concurrent.Future

object EmptyMoment extends Moment[Any,Nothing] with DelegatedProjection[Any,Nothing] {
  val delegate = EmptyProjection
  val active = EmptyProjection
  val inactive = EmptyProjection
  val all = EmptyProjection

  override def filterKeys(f: Any => Boolean) = this

  override val materialize: Future[MaterializedMoment[Any, Nothing]] =
    MaterializedMoment.empty[Any,Nothing].future
}
