package s_mach.aeondb.internal

import scala.concurrent.Future

trait FutureMoment[A,B,PB] {
  def find(key: A) : Future[Option[B]]

  def deactivate(key: A) : FutureMoment[A,B,PB]
  def reactivate(key: A, value: B) : FutureMoment[A,B,PB]

  def put(key: A, value: B) : FutureMoment[A,B,PB]
  def replace(key: A, value: B) : FutureMoment[A,B,PB]
}

