package s_mach.aeondb.impl

import s_mach.datadiff._
import s_mach.aeondb.internal.FutureMoment

case class FutureMomentImpl[A,B,PB](
  base: OldMomentImpl[A,B,PB]
)(implicit
  bDataDiff:DataDiff[B,PB]
  ) extends FutureMoment[A,B,PB] {
  val builder = CommitBuilder[A,B,PB]()

  override def put(
    key: A,
    value: B
  ): FutureMoment[A,B,PB] = {
    builder.put(key,value)
    this
  }

  override def replace(
    key: A,
    value: B
  ): FutureMoment[A,B,PB] = {
    val record = base.local.active(key)
    val patch = record.value calcDiff value
    builder.replace(key,patch,record.version)
    this
  }

  override def reactivate(
    key: A,
    value: B
  ): FutureMoment[A,B,PB] = {
    val record = base.local.inactive(key)
    builder.reactivate(key,value,record.version)
    this
  }

  override def deactivate(
    key: A
  ): FutureMoment[A,B,PB] = {
    builder.deactivate(key, base.local.active(key).version)
    this
  }

  override def find(key: A) = {
    val record = base.local.active(key)
    builder.checkout(key,record.version)
    base.find(key)
  }
}

