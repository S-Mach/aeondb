package s_mach.aeondb.impl

import s_mach.aeondb._

case class BaseMomentImpl[A,B,PB](
  aeonMap:AeonMapImpl[A,B,PB],
  aeon: Aeon,
  local: MaterializedMoment[A,B]
) extends OldMomentImpl[A,B,PB] with LiftedLocalMoment[A,B,MaterializedMoment[A,B]] {
  override def oomCommit = Nil

  override def filterKeys(f: A => Boolean) =
    copy(local = local.filterKeys(f))
}
