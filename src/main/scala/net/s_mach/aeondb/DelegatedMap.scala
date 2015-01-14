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

import scala.collection.immutable.{DefaultMap, AbstractMap}

abstract class DelegatedMap[A,+B] extends AbstractMap[A,B] with DefaultMap[A,B] { self =>
  def delegate: Map[A,B]

  override def foreach[C](f: ((A, B)) => C) = delegate.foreach(f)
  def iterator = delegate.iterator
  override def size = delegate.size
  override def contains(key: A) = delegate.contains(key)
  def get(key: A) = delegate.get(key)
}

abstract class DelegatedUnionMap2[A,+B] extends AbstractMap[A,B] with DefaultMap[A,B] { self =>
  def delegate1: Map[A,B]
  def delegate2: Map[A,B]

  override def foreach[C](f: ((A, B)) => C) = {
    delegate1.foreach(f)
    delegate2.foreach(f)
  }
  def iterator = delegate1.iterator ++ delegate2.iterator
  override def size = delegate1.size + delegate2.size
  override def contains(key: A) = delegate1.contains(key) || delegate2.contains(key)
  def get(key: A) = delegate1.get(key) orElse delegate2.get(key)
  override lazy val keys = delegate1.keys ++ delegate2.keys
}

