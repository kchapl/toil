package model

import scala.annotation.tailrec

object GapFiller {

  def fillGaps[A](as: Seq[A], next: A => A): Seq[A] = {

    @tailrec
    def go(toGo: Seq[A], soFar: Seq[A] = Nil): Seq[A] = {
      toGo match {
        case curr :: tl =>
          soFar.lastOption match {
            case Some(prev) =>
              if (next(prev) != curr) {
                go(toGo, soFar :+ next(prev))
              } else {
                go(tl, soFar :+ curr)
              }
            case None =>
              go(tl, Seq(curr))
          }
        case Nil => soFar
      }
    }

    go(as)
  }
}
