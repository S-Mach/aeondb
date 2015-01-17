package s_mach.aeondb.impl

import org.joda.time.Instant
import s_mach.aeondb._

case class EmptyOldMomentImpl[A,B,PB](
  aeonMap: AeonMap[A,B,PB],
  endTime: Instant,
  // Note: using case class args to prevent init order NPE here
  local:LocalMoment[A,B] = LocalMoment.empty[A,B],
  oomCommit: List[(Commit[A,B,PB],Metadata)] = Nil
) extends OldMomentImpl[A,B,PB] with LiftedLocalMoment[A,B,LocalMoment[A,B]] {
  val aeon = Aeon(
    beginOfTime,
    endTime
  )
  override def filterKeys(f: A => Boolean) = this
}
