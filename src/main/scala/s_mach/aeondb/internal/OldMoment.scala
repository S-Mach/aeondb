package s_mach.aeondb.internal

import s_mach.aeondb._

import scala.concurrent.Future

trait OldMoment[A,B,PB] extends Moment[A,B] {

  override def filterKeys(f: (A) => Boolean): OldMoment[A,B,PB]

  def aeonMap:AeonMap[A,B,PB]

  def aeon: Aeon

  def checkout() : Future[AeonMap[A,B,PB]]

//    def oomCommit: Future[List[(Commit[A,B,PB],Metadata)]]
}

