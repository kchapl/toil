package model

import java.time.LocalDate
import java.time.LocalDate.now

import model.Amount.zero

import scala.annotation.tailrec

case class AccountAndTransactions(account: Account, transactions: Set[Transaction]) {

  val dateBalances: Seq[DateAmount] = {

    @tailrec
    def go(left: Seq[DateAmount], soFar: Seq[DateAmount], prevBalance: Amount): Seq[DateAmount] = {
      left match {
        case hd :: tl =>
          val currBalance = Amount.sum(Seq(prevBalance, hd.amount))
          go(tl, soFar :+ hd.copy(amount = currBalance), currBalance)
        case Nil => soFar
      }
    }

    val diffs = {
      val dateAmounts = DateAmount.fromTransactions(transactions)
      dateAmounts.lastOption
        .filter { dateAmount =>
          dateAmount.date.isBefore(now)
        }
        .map { _ =>
          dateAmounts :+ DateAmount(now, zero)
        }
        .getOrElse {
          dateAmounts
        }
        .foldLeft(Seq.empty[DateAmount]) { (soFar, amount) =>
          soFar ++ soFar.lastOption.map(last => filledIn(last, amount)).getOrElse(Nil) :+ amount
        }
    }

    go(diffs, Nil, account.originalBalance)
  }

  private def filledIn(a: DateAmount, b: DateAmount): Seq[DateAmount] = {

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

  private val transactionSeq = transactions.toSeq

  val latestTransaction: Transaction = transactionSeq.maxBy(_.date.toString)

  val balance: Amount = Amount.sum(transactionSeq.map(_.amount) :+ account.originalBalance)
}
