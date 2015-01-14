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

trait DelegatedUnionLocalProjection[A,+B] extends LocalProjection[A,B] { self =>
  def delegate1: Map[A,B]
  def delegate2: Map[A,B]

  override def size = delegate1.size + delegate2.size
  override lazy val keys = delegate1.keys ++ delegate2.keys
  override def find(key: A) = delegate1.get(key) orElse delegate2.get(key)
  override def toMap = delegate1 ++ delegate2
}

object DelegatedUnionLocalProjection {
  case class DelegatedUnionLocalProjectionImpl[A,B](
    delegate1: Map[A,B],
    delegate2: Map[A,B]
  ) extends DelegatedUnionLocalProjection[A,B] {
    override def filterKeys(f: (A) => Boolean) =
      DelegatedUnionLocalProjectionImpl(
        delegate1 = delegate1.filterKeys(f),
        delegate2 = delegate2.filterKeys(f)
      )
  }
  def apply[A,B](
    delegate1: Map[A,B],
    delegate2: Map[A,B]
  ) : DelegatedUnionLocalProjection[A,B] =
    DelegatedUnionLocalProjectionImpl(delegate1, delegate2)
}