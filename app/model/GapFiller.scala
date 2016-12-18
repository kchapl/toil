package model

import scala.annotation.tailrec

object GapFiller {

  def fillGaps[A](as: Seq[A], isGapBetween: (A, A) => Boolean, next: A => A): Seq[A] = {

    @tailrec
    def go(toGo: Seq[A], soFar: Seq[A] = Nil): Seq[A] = {
      toGo match {
        case hd :: tl =>
          soFar.lastOption match {
            case Some(c) =>
              if (isGapBetween(c, hd)) {
                go(toGo, soFar :+ next(c))
              } else {
                go(tl, soFar :+ hd)
              }
            case None =>
              go(tl, Seq(hd))
          }
        case Nil => soFar
      }
    }

    go(as)
  }

  println(
    fillGaps[Int](
      Seq(1, 5), { case (a, b) => a < b + 1 }, { _ + 1 }
    )
  )

  println(
    fillGaps[Char](
      Seq('a', 'd'), { case (a, b) => a.toInt < (b.toInt + 1) }, { a => (a.toInt + 1).toChar }
    )
  )
}
