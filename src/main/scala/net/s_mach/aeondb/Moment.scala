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

import net.s_mach.aeondb.impl.EmptyMoment

import scala.concurrent.Future

trait Moment[A,+B] extends Projection[A,B] {
  override def filterKeys(f: (A) => Boolean): Moment[A,B]

  def active : Projection[A,Record.Active[B]]
  def inactive : Projection[A,Record.Inactive]
  def all : Projection[A,Record[B]]

  def materialize() : Future[MaterializedMoment[A,B]]
}

object Moment {
  private[this] val _empty = EmptyMoment
  def empty[A,B] = _empty.asInstanceOf[Moment[A,B]]
}