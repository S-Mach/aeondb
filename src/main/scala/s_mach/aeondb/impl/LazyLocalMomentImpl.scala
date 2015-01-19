package s_mach.aeondb.impl

import s_mach.aeondb.Record

case class LazyLocalMomentImpl[A,B]()(
  calcActive: Map[A,Record.Active[B]],
  calcInactive: Map[A,Record.Inactive] = Map.empty[A,Record.Inactive]
) extends DelegatedLocalProjection[A,B] with LazyLocalMoment[A,B] {
  lazy val active = calcActive
  lazy val inactive = calcInactive
  lazy val delegate = active.mapValues(_.value)
  lazy val all = active ++ inactive

  override def filterKeys(f: (A) => Boolean) =
    LazyLocalMomentImpl()(
      calcActive = active.filterKeys(f),
      calcInactive = inactive.filterKeys(f)
    )
}
