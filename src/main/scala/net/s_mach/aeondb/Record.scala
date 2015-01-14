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

sealed trait Record[+A] {
  def isActive: Boolean
  def version: Long
}

object Record {
  case class Inactive(
    version: Long
  ) extends Record[Nothing] {
    override def isActive = false
  }

  sealed trait Active[+A] extends Record[A] {
    def value: A

    def materialize : Record.Materialized[A]
  }

  case class Materialized[+A](
    value: A,
    version: Long = 1
  ) extends Active[A] {
    override def isActive = true
    override def materialize = this
  }

  case class Lazy[+A]()(
    calcValue: => A,
    calcVersion: => Long
  ) extends Active[A] {
    lazy val value = calcValue
    lazy val version = calcVersion

    override def isActive = true

    def materialize = Materialized(
      value = value,
      version = version
    )
  }

  def apply[A](
    value: A,
    version: Long = 1,
    zomPrev: List[Record[A]] = Nil
  ) : Materialized[A] = Materialized(
    value = value,
    version = version
  )

  def lazyApply[A](
    calcValue: => A,
    calcVersion: => Long = 1
  ) : Lazy[A] = {
    Lazy()(
      calcValue = calcValue,
      calcVersion = calcVersion
    )
  }
}

