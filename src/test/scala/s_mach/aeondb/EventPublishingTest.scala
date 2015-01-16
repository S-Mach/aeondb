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

import s_mach.aeondb.AeonMap.{OnCommit, EventHandler}
import s_mach.aeondb.impl.CommitBuilder
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.{FlatSpec, Matchers}
import s_mach.concurrent._

class EventPublishingTest extends FlatSpec with Matchers {
  implicit val metadata = Metadata(
    who = "test",
    why = Some("test")
  )

//  "EventPublishing" must "must publish an event for each change" in {
//    val m = Map(1 -> "a", 2 -> "b")
//    val allEvents = mutable.ListBuffer[AeonMap.Event[Int,String,Option[String]]]()
//    val subscriber : EventHandler[Int,String,Option[String]] = { events =>
//      allEvents ++= events
//    }
//    val p = AeonMap(subscriber)(m.toSeq:_*)
//    p.now.toMap.get should equal(m)
//    p.now.put(3,"c").get
//    p.now.replace(1,"aa").get
//    p.now.deactivate(2).get
//    p.now.reactivate(2,"bb").get
//    Thread.sleep(100)
//    allEvents.result()(0) should equal(
//      OnCommit((CommitBuilder().put(3,"c").result()._2,metadata) :: Nil)
//    )
////    allEvents.result() should equal(
////      (CommitBuilder().put(3,"c").result()._2,metadata) ::
////        (CommitBuilder().replace(1,Some("aa"),1).result()._2,metadata) ::
////        (CommitBuilder().deactivate(2,1).result()._2,metadata) ::
////        (CommitBuilder().reactivate(2,"bb",2).result()._2,metadata) ::
////      Nil
////    )
//  }

}
