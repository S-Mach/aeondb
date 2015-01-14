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

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.{FlatSpec, Matchers}
import s_mach.aeondb.impl.AeonMapImpl
import s_mach.concurrent._

class AeonMapNowTest extends FlatSpec with Matchers {
  "AeonMapTest" must "convert now toMap" in {
    val m = Map(1 -> "a", 2 -> "b")
    val p = AeonMap(m.toSeq:_*)
    p.now.toMap.get should equal(m)
  }
}
