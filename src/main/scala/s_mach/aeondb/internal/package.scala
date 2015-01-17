package s_mach.aeondb

package object internal {
  type EventHandler[A,B,PB] = Seq[Event[A,B,PB]] => Unit
}
