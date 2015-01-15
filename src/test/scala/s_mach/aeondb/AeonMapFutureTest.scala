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

import s_mach.aeondb.impl.CommitBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.{FlatSpec, Matchers}
import s_mach.concurrent._

class AeonMapFutureTest extends FlatSpec with Matchers {
  implicit val metadata = Metadata(
    who = "test",
    why = Some("test")
  )

  "AeonMapTest.future" must "put" in {
    val m = Map(1 -> "a", 2 -> "b")
    val p = AeonMap(m.toSeq:_*)
    p.future { f =>
      f.put(3,"c").future
    }.get should equal(true)
    p.now.toMap.get should equal(m + (3 -> "c"))
    p.zomCommit.get should equal(
      (CommitBuilder().put(3,"c").result()._2,metadata) ::
      Nil
    )
  }
  
  "AeonMap.now" must "replace" in {
    val m = Map(1 -> "a", 2 -> "b")
    val p = AeonMap(m.toSeq:_*)
    p.now.replace(1,"aa").get should equal(true)
    p.now.replace(3,"cc").get should equal(false)
    p.now.toMap.get should equal(m - 1 + (1 -> "aa"))
    p.zomCommit.get should equal(
      (CommitBuilder().replace(1,Some("aa"),1).result()._2,metadata) ::
      Nil
    )
  }

  "AeonMap.future" must "deactivate" in {
    val m = Map(1 -> "a", 2 -> "b")
    val p = AeonMap(m.toSeq:_*)
    p.future { f =>
      f.deactivate(1).future
    }.get should equal(true)
    p.now.toMap.get should equal(m - 1)
    p.now.deactivate(1).get should equal(false)
    p.now.deactivate(3).get should equal(false)
    p.zomCommit.get should equal(
      (CommitBuilder().deactivate(1,1).result()._2,metadata) ::
      Nil
    )
  }

  "AeonMap.future" must "reactivate" in {
    val m = Map(1 -> "a", 2 -> "b")
    val p = AeonMap(m.toSeq:_*)
    p.now.deactivate(1).get should equal(true)
    p.now.toMap.get should equal(m - 1)
    p.future { f =>
      f.reactivate(1,"aa").future
    }.get should equal(true)
    p.now.toMap.get should equal(m - 1 +(1 -> "aa"))
    p.now.reactivate(1,"aaa").get should equal(false)
    p.now.reactivate(3,"c").get should equal(false)
    p.zomCommit.get should equal(
      (CommitBuilder().reactivate(1,"aa",2).result()._2,metadata) ::
      (CommitBuilder().deactivate(1,1).result()._2,metadata) ::
      Nil
    )
  }

  "AeonMap.future" must "allow combining all operations into one commit" in {
    val m = Map(1 -> "a", 2 -> "b", 3 -> "c")
    val p = AeonMap(m.toSeq:_*)
    p.now.deactivate(1).get should equal(true)
    p.now.toMap.get should equal(m - 1)
    p.future { _
        .put(4,"d")
        .replace(3,"cc")
        .deactivate(2)
        .reactivate(1,"aa").future
    }.get should equal(true)
    p.now.toMap.get should equal(Map(1 -> "aa", 3 -> "cc", 4 -> "d"))
    p.zomCommit.get should equal(
      (
        CommitBuilder()
          .put(4,"d")
          .replace(3,Some("cc"),1)
          .deactivate(2,1)
          .reactivate(1,"aa",2)
          .result()._2,
        metadata
      ) ::
      (CommitBuilder().deactivate(1,1).result()._2,metadata) ::
      Nil
    )
  }
}
