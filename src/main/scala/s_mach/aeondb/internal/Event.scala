package s_mach.aeondb.internal

import s_mach.aeondb._

sealed trait Event[A,B,PB]

case class CommitEvent[A,B,PB](
  oomCommit: List[(Commit[A,B,PB],Metadata)]
) extends Event[A,B,PB]
