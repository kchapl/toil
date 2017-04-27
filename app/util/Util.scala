package util

object Util {

  def sequence[A, B](es: => Seq[Either[A, B]]): Either[A, Seq[B]] =
    es.foldRight(Right(Nil): Either[A, List[B]]) { (e, acc) =>
      for {
        xs <- acc.right
        x <- e.right
      } yield x :: xs
    }

  def asOption(s: String): Option[String] = if (s.isEmpty) None else Some(s)

  def asOption[A](xs: Seq[A]): Option[Seq[A]] = if (xs.isEmpty) None else Some(xs)
}
