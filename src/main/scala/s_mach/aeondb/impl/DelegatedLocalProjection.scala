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

import s_mach.aeondb.LocalProjection

trait DelegatedLocalProjection[A,+B] extends LocalProjection[A,B] { self =>
  def delegate: Map[A,B]

  override def size = delegate.size
  override def keys = delegate.keys
  override def find(key: A) = delegate.get(key)
  override def toMap = delegate
}

object DelegatedLocalProjection {
  case class DelegatedLocalProjectionImpl[A,B](
    delegate: Map[A,B]
  ) extends DelegatedLocalProjection[A,B] {
    override def filterKeys(f: (A) => Boolean) =
      DelegatedLocalProjection(delegate.filterKeys(f))
  }
  def apply[A,B](delegate: Map[A,B]) : DelegatedLocalProjection[A,B] =
    DelegatedLocalProjectionImpl(delegate)
}

