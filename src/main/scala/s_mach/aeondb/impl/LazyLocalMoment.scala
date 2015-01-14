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

import s_mach.aeondb._

trait LazyLocalMoment[A,+B] extends LocalMoment[A,B] { self =>
  override lazy val materialize = {
    MaterializedMoment(
      active = active.map { case (key,record) => (key, record.materialize) }.toMap,
      inactive = inactive
    )
  }
  lazy val asMoment : Moment[A,B] = LiftedLocalMoment[A,B,LazyLocalMoment[A,B]](this)
}

object LazyLocalMoment {
  private[this] val _empty = LazyLocalMomentImpl[Any,Nothing]()(Map.empty)
  def empty[A,B] = _empty.asInstanceOf[LazyLocalMoment[A,B]]

  case class LazyLocalMomentImpl[A,B]()(
    calcActive: Map[A,Record.Active[B]],
    calcInactive: Map[A,Record.Inactive] = Map.empty[A,Record.Inactive]
  ) extends DelegatedLocalProjection[A,B] with LazyLocalMoment[A,B] {
    lazy val active = calcActive
    lazy val inactive = calcInactive
    lazy val delegate = active.mapValues(_.value)
    lazy val all = active ++ inactive

    override def filterKeys(f: (A) => Boolean) =
      LazyLocalMomentImpl()(
        calcActive = active.filterKeys(f),
        calcInactive = inactive.filterKeys(f)
      )
  }

  def apply[A,B](kv: (A,B)*) : LazyLocalMoment[A,B] =
    LazyLocalMomentImpl[A,B]()(
      calcActive = kv.map { case (key,value) => (key, Record.lazyApply(value))}.toMap
    )

  def apply[A,B](
    calcActive: => Map[A,Record.Active[B]],
    calcInactive: => Map[A,Record.Inactive]
  ) : LazyLocalMoment[A,B] =
    LazyLocalMomentImpl[A,B]()(
      calcActive = calcActive,
      calcInactive = calcInactive
    )
}
