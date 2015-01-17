package s_mach.aeondb.impl

import scala.collection.mutable
import s_mach.aeondb._
import s_mach.datadiff._

case class LazyOldMomentImpl[A,B,PB](
  aeonMap: AeonMapImpl[A,B,PB],
  aeon: Aeon,
  oomCommit: List[(Commit[A,B,PB],Metadata)],
  optFilterKeys: Option[A => Boolean] = None
)(implicit
  bDataDiff:DataDiff[B,PB]
) extends OldMomentImpl[A,B,PB] with LiftedLocalMoment[A,B,LazyLocalMoment[A,B]] {

  override def filterKeys(f: (A) => Boolean) =
    copy(optFilterKeys = Some(f))

  def calcPrev = aeonMap.old(aeon.start.minus(1))

  // Note: prev is not saved to prevent holding long references to previous
  // old moments - don't close over prev as a val!
//  def calcPrev = old(aeon.start.minus(1))

  val local = {
    def maybeFilterKeys[C](m:Map[A,C]) : Map[A,C] = {
      optFilterKeys match {
        case Some(f) => m.filterKeys(f)
        case None => m
      }
    }
    LazyLocalMoment(
      calcActive = {
        // Note: not closing over builder so that it can be discarded
        val builder = mutable.Map[A,Record.Active[B]](
          maybeFilterKeys(calcPrev.local.active).toSeq:_*
        )
        oomCommit.foreach { case (rawCommit,_) =>
          val commit = {
            optFilterKeys match {
              case Some(f) => rawCommit.filterKeys(f)
              case None => rawCommit
            }
          }
          commit.put.foreach { case (key,value) =>
            builder.put(key,Record(value))
          }
          commit.replace.foreach { case (key,patch) =>
            lazy val record = calcPrev.local.active(key)
            lazy val calcValue = record.value applyPatch patch
            lazy val calcVersion = record.version + 1
            builder.put(key, Record.lazyApply(
              calcValue = calcValue,
              calcVersion = calcVersion
            ))
          }
          commit.deactivate.foreach(builder.remove)
          commit.reactivate.foreach { case (key,value) =>
            lazy val record = calcPrev.local.inactive(key)
            lazy val calcVersion = record.version + 1
            builder.put(key, Record.lazyApply(
              calcValue = value,
              calcVersion = calcVersion
            ))
          }
        }
        builder.toMap
      },
      calcInactive = {
        // Note: not closing over builder so that it can be discarded
        val builder = mutable.Map[A,Record.Inactive](
          maybeFilterKeys(calcPrev.local.inactive).toSeq:_*
        )
        oomCommit.foreach { case (commit,_) =>
          commit.reactivate.foreach { case (k,_) => builder.remove(k) }
          commit.deactivate.foreach { key =>
            val record = calcPrev.local.active(key)
            builder.put(key, Record.Inactive(record.version + 1))
          }
        }
        builder.toMap
      }
    )
  }
}

