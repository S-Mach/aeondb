package s_mach.aeondb.internal

sealed trait Error[+A] extends Exception
case class KeyNotFoundError[+A](
  oomKeyNotFound: Iterable[A]
) extends Error[A]
case class EmptyCommitError() extends Error[Nothing]

case class VersionMismatch[+A](
  key: A,
  expectedVersion: Long,
  version: Long
)

sealed trait MergeError[+A] extends Error[A]
case class MergeConflictError[+A](
  oomMergeConflict: Iterable[VersionMismatch[A]]
) extends MergeError[A]

sealed trait CommitError[+A] extends Error[A]

sealed trait PutError[+A] extends Error[A]
case class KeyAlreadyExists[+A](key: A) extends PutError[A]
