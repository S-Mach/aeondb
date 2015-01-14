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

import org.joda.time.{Interval, Instant}

// Note: using this since org.joda.time.Interval discards the Instant
// instances. Round trip from (Instant,Instant) to Interval to
// (Instant,Instant) means throwing away original instances, creating temp
// Interval and creating two new Instant instances
case class Aeon(
  start: Instant,
  end: Instant
) {
  def contains(other: (Instant, Instant)) : Boolean = {
    val lhsBegin = start.getMillis
    val lhsEnd = end.getMillis
    val rhsBegin = other._2.getMillis
    val rhsEnd = other._2.getMillis

    lhsBegin <= rhsBegin &&
    rhsBegin < lhsEnd &&
    rhsEnd < lhsEnd
  }
  def contains(other: Instant) : Boolean = {
    val when = other.getMillis
    start.getMillis <= when && when <= end.getMillis
  }
  def toInterval : Interval = new Interval(start,end)
}


