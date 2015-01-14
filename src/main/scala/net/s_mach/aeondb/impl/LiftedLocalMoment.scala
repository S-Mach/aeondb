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

import scala.language.higherKinds
import net.s_mach.aeondb._
import s_mach.concurrent._

trait LiftedLocalMoment[A,+B,+LM <: LocalMoment[A,B]] extends Moment[A,B] with LiftedLocalProjection[A,B] { self =>
  def local:LM
  // Note: these have to be lazy to avoid init order NPE
  override lazy val active = LiftedMapProjection(self.local.active)
  override lazy val inactive = LiftedMapProjection(self.local.inactive)
  override lazy val all = LiftedMapProjection(self.local.all)

  override def materialize() = local.materialize.future
}

object LiftedLocalMoment {
  case class LiftedLocalMomentImpl[A,B,+LM <: LocalMoment[A,B]](
    local:LM
  ) extends LiftedLocalMoment[A,B,LM] {
    override def filterKeys(f: (A) => Boolean) =
      LiftedLocalMomentImpl[A,B,LocalMoment[A,B]](local.filterKeys(f))
  }

  def apply[A,B,LM <: LocalMoment[A,B]](self:LM) : Moment[A,B] =
    LiftedLocalMomentImpl[A,B,LM](self)
}

