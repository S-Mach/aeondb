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
package net.s_mach.aeondb

import net.s_mach.aeondb.impl.LiftedLocalMoment

trait MaterializedMoment[A,+B] extends LocalMoment[A,B] {
  override def filterKeys(f: (A) => Boolean): MaterializedMoment[A,B]

  override def active : Map[A,Record.Materialized[B]]

  override def materialize = this
  lazy val asMoment : Moment[A,B] = LiftedLocalMoment[A,B,MaterializedMoment[A,B]](this)
}

object MaterializedMoment {
  private[this] val _empty = MaterializedMomentImpl[Any,Nothing](Map.empty)
  def empty[A,B] = _empty.asInstanceOf[MaterializedMoment[A,B]]

  case class MaterializedMomentImpl[A,B](
    active: Map[A,Record.Materialized[B]],
    inactive: Map[A,Record.Inactive] = Map.empty[A,Record.Inactive]
  ) extends DelegatedLocalProjection[A,B] with
    MaterializedMoment[A,B] {
    val delegate = active.mapValues(_.value)
    val all = new DelegatedUnionMap2[A,Record[B]] {
      def delegate1 = active
      def delegate2 = inactive
    }
    override def filterKeys(f: A => Boolean) =
      MaterializedMomentImpl(
        active = active.filterKeys(f),
        inactive = inactive.filterKeys(f)
      )
  }

  def apply[A,B](kv: (A,B)*) : MaterializedMoment[A,B] =
    MaterializedMomentImpl[A,B](
      active = kv.map { case (key,value) => (key, Record(value))}.toMap
    )

  def apply[A,B](
    active: Map[A,Record.Materialized[B]],
    inactive: Map[A,Record.Inactive]
  ) : MaterializedMoment[A,B] =
    MaterializedMomentImpl[A,B](
      active = active,
      inactive = inactive
    )
}