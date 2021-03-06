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
package s_mach.aeondb.impl

import s_mach.concurrent._
import s_mach.aeondb.{LocalProjection, Projection}

trait LiftedLocalProjection[A,+B] extends Projection[A,B] {
  def local: LocalProjection[A,B]

  override def size = local.size.future
  override def find(key: A) = local.find(key).future
  override def keys = local.keys.future
  override def toMap = local.toMap.future
}

trait LiftedMapProjection[A,+B] extends Projection[A,B] {
  def local: Map[A,B]

  override def size = local.size.future
  override def find(key: A) = local.get(key).future
  override def keys = local.keys.future
  override def toMap = local.toMap.future
}

object LiftedMapProjection {
  case class LiftedMapProjectionImpl[A,+B](
    local: Map[A,B]
  ) extends LiftedMapProjection[A,B] {
    override def filterKeys(f: (A) => Boolean) =
      LiftedMapProjectionImpl(local.filterKeys(f))
  }
  def apply[A,B](local: Map[A,B]) : LiftedMapProjection[A,B] =
    LiftedMapProjectionImpl(local)
}