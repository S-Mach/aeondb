package s_mach.aeondb.impl

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.{ExecutionContext, Future}
import s_mach.concurrent._
import s_mach.aeondb.internal._
import s_mach.aeondb.{AeonMap, Commit, Metadata}

trait EventPublishing[A,B,PB] extends AeonMap[A,B,PB] {
  
  implicit def executionContext:ExecutionContext
  def oomSubscriber: Traversable[EventHandler[A,B,PB]]

  private[this] val eventQueue = new ConcurrentLinkedQueue[Event[A,B,PB]]()
  private[this] val publishInProgress = new AtomicBoolean(false)

  private[this] def publishEvents() : Unit = {
    def loop(events: List[Event[A,B,PB]]) : List[Event[A,B,PB]] = {
      eventQueue.poll() match {
        case e@CommitEvent(_) =>
          loop(e :: events)
        case _ =>
          events
      }
    }
    val events = loop(Nil)
    if(events.nonEmpty ) {
      oomSubscriber.foreach(_(events))
    }
    publishInProgress.set(false)
  }

  protected override def unsafeOnCommitHook(
    oomCommit: List[(Commit[A,B,PB],Metadata)]
  ) : Unit = {
    eventQueue.offer(CommitEvent(oomCommit))
    if(publishInProgress.compareAndSet(false,true)) {
      Future { publishEvents() }.background
    }
  }
}