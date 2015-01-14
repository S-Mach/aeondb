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
package net.s_mach.aeondb.impl

import net.s_mach.aeondb._
import net.s_mach.aeondb.diffmap.Commit

class CommitBuilder[A,B,PB] {
  private[this] val _checkout = Map.newBuilder[A,Long]
  private[this] val _put = Map.newBuilder[A,B]
  private[this] val _replace = Map.newBuilder[A,PB]
  private[this] val _deactivate = Set.newBuilder[A]
  private[this] val _reactivate= Map.newBuilder[A,B]

  def checkout(key: A, version: Long) = {
    _checkout.+=((key,version))
    this
  }

  def put(
    key: A,
    value: B
  ) = {
    _put.+=((key,value))
    this
  }

  def replace(
    key: A,
    version: Long,
    patch: PB
  ) = {
    _checkout.+=((key,version))
    _replace.+=((key,patch))
    this
  }

  def deactivate(key:A, version:Long) = {
    _checkout.+=((key,version))
    _deactivate += key
    this
  }

  def reactivate(
    key:A,
    value:B,
    version:Long
  ) = {
    _checkout.+=((key,version))
    _reactivate += ((key,value))
    this
  }

  def result() : (Checkout[A], Commit[A,B,PB]) = {
    val checkout = _checkout.result()
    val put = _put.result()
    val replace = _replace.result()
    val deactivate = _deactivate.result()
    val reactivate = _reactivate.result()
    require(
      replace.keySet.forall(checkout.contains),
      "All changed ids must be checked out"
    )
    require(
      deactivate.forall(checkout.contains),
      "All deactivated ids must be checked out"
    )
    require(
      reactivate.forall { case (k,_) => checkout.contains(k) },
      "All reactivated ids must be checked out")

    (
      checkout,
      Commit(
        put = put,
        replace = replace,
        deactivate = deactivate,
        reactivate = reactivate
      )
    )
  }
}

object CommitBuilder {
  def apply[A,B,PB]() : CommitBuilder[A,B,PB] = new CommitBuilder[A,B,PB]
}

