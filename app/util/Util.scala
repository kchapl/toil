package util

object Util {

  def sequence[A, B](s: => Seq[Either[A, B]]): Either[A, Seq[B]] =
    s.foldRight(Right(Nil): Either[A, List[B]]) {
      (e, acc) => for (xs <- acc.right; x <- e.right) yield x :: xs
    }

  def opt(s: String) = if (s.isEmpty) None else Some(s)
}
