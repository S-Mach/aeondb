/*
                    ,i::,
               :;;;;;;;
              ;:,,::;.
            1ft1;::;1tL
              t1;::;1,
               :;::;               _____       __  ___              __
          fCLff ;:: tfLLC         / ___/      /  |/  /____ _ _____ / /_
         CLft11 :,, i1tffLi       \__ \ ____ / /|_/ // __ `// ___// __ \
         1t1i   .;;   .1tf       ___/ //___// /  / // /_/ // /__ / / / /
       CLt1i    :,:    .1tfL.   /____/     /_/  /_/ \__,_/ \___//_/ /_/
       Lft1,:;:       , 1tfL:
       ;it1i ,,,:::;;;::1tti      AeonDB
         .t1i .,::;;; ;1tt        Copyright (c) 2014 S-Mach, Inc.
         Lft11ii;::;ii1tfL:       Author: lance.gatlin@gmail.com
          .L1 1tt1ttt,,Li
            ...1LLLL...
*/
package s_mach.aeondb.diffmap

case class Commit[A,+B,+PB](
  put: Map[A,B] = Map.empty[A,B],
  replace: Map[A,PB] = Map.empty[A,PB],
  deactivate: Set[A] = Set.empty[A],
  reactivate: Map[A,B] = Map.empty[A,B]
) {
  def isNoChange : Boolean =
    put.size == 0 &&
    replace.size == 0 &&
    deactivate.size == 0 &&
    reactivate.size == 0

  def filterKeys(f: A => Boolean) : Commit[A,B,PB] = {
    copy(
      put = put.filterKeys(f),
      replace = replace.filterKeys(f),
      deactivate = deactivate.filter(f),
      reactivate = reactivate.filterKeys(f)
    )
  }
}

object Commit {
  private[this] val _noChange = Commit[Any,Nothing,Nothing]()
  def noChange[A,B,PB] : Commit[A,B,PB] = _noChange.asInstanceOf[Commit[A,B,PB]]
}
