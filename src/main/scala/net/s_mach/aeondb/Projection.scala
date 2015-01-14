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

import net.s_mach.aeondb.impl.EmptyProjection

import scala.concurrent.Future

trait Projection[A,+B] {
  // Operations
  def size: Future[Int]
  def find(key: A) : Future[Option[B]]
  //def find(key: A*) : Future[Option[B]]
  // def findOrElse

  // * Operations
  def keys: Future[Iterable[A]]
  // def keySet
  // def keysIterator

  def toMap: Future[Map[A,B]]
  // def iterator
  // def aggregate
  // def reduce
  // def reduceOption
  // def scan
  // def collect
  // def collectFirst
  // def copyToArray
  // def copyToBuffer
  // def count
  // def exists
  // def equals
  // def sameElements
  // def map
  // def flatMap
  // def fold
  // def forall
  // def foreach *can't cluster compute side effects
  // def groupBy
  // def isEmpty
  // def nonEmpty
  // def max/min
  // def maxBy/minBy
  // def sum
  // def values
  // def valuesIterator
  // def unzip
  // def transform

  // def toArray
  // def toBuffer
  // toIndexedSeq
  // toIterable
  // toIterator
  // toList
  // toMap
  // toSeq
  // toSet
  // toStream
  // toTraversable
  // toVector

  // Query modifiers
  def filterKeys(f: A => Boolean) : Projection[A,B]
  // def filter
  // def filterNot
  // def mapValues

}

object Projection {
  private[this] val _empty = EmptyProjection
  def empty[A,B] = _empty.asInstanceOf[Projection[A,B]]
}