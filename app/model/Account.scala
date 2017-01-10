package model

import java.time.LocalDate

import scala.annotation.tailrec

case class Account(name: String, originalBalance: Amount, transactions: Set[Transaction]) {

  val dateBalances: Seq[DateAmount] = {

    @tailrec
    def go(
      left: Seq[DateAmount],
      soFar: Seq[DateAmount] = Nil,
      prevBalance: Amount = originalBalance
    ): Seq[DateAmount] = {
      left match {
        case hd :: tl =>
          val currBalance = Amount.sum(prevBalance, hd.amount)
          go(tl, soFar :+ hd.copy(amount = currBalance), currBalance)
        case Nil => soFar
      }
    }

    val diffs = DateAmount.fromTransactions(transactions).foldLeft(Seq.empty[DateAmount]) {
      (soFar, curr) =>
        soFar ++ soFar.lastOption.map(a => filling(a, curr)).getOrElse(Nil) :+ curr
    }

    go(diffs)
  }

  private def filling(a: DateAmount, b: DateAmount): Seq[DateAmount] = {

    val end: LocalDate = b.date

    @tailrec
    def go(start: LocalDate, soFar: Seq[DateAmount]): Seq[DateAmount] = {
      if (start == end) soFar
      else {
        soFar.lastOption match {
          case Some(prev) =>
            if (prev.date.plusDays(1).isBefore(end)) {
              go(start.plusDays(1), soFar :+ DateAmount(start, Amount(0)))
            } else {
              soFar
            }
          case None =>
            go(start.plusDays(1), Seq(DateAmount(start, Amount(0))))
        }
      }
    }

    go(a.date.plusDays(1), Nil)
  }
}
