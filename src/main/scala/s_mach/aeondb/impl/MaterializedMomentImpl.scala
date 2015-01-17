package s_mach.aeondb.impl

import s_mach.aeondb._

case class MaterializedMomentImpl[A,B](
  active: Map[A,Record.Materialized[B]],
  inactive: Map[A,Record.Inactive] = Map.empty[A,Record.Inactive]
) extends DelegatedLocalProjection[A,B] with
  MaterializedMoment[A,B] {
  val delegate = active.mapValues(_.value)
  val all = new DelegatedUnionMap2[A,Record[B]] {
    def delegate1 = active
    def delegate2 = inactive
  }
  override def filterKeys(f: A => Boolean) =
    MaterializedMomentImpl(
      active = active.filterKeys(f),
      inactive = inactive.filterKeys(f)
    )
}

