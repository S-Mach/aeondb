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

trait LocalMoment[A,+B] extends LocalProjection[A,B] {
  override def filterKeys(f: (A) => Boolean): LocalMoment[A,B]

  def active : Map[A,Record.Active[B]]
  def inactive : Map[A,Record.Inactive]
  def all: Map[A,Record[B]]

  def materialize : MaterializedMoment[A,B]
  def asMoment: Moment[A,B]
}

object LocalMoment {
  private[this] val _empty = MaterializedMoment.empty[Any,Nothing]
  def empty[A,B] = _empty.asInstanceOf[LocalMoment[A,B]]
}