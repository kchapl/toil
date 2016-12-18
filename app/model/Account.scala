package model

import scala.annotation.tailrec

case class Account(name: String, originalBalance: Amount, transactions: Set[Transaction]) {

  val balances: Seq[DateAmount] = {

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

    val diffs = transactions.groupBy(_.date).map {
      case (date, txs) => DateAmount(date, Amount.sum(txs.map(_.amount).toSeq: _*))
    }.toSeq.sortBy(_.date.toEpochDay)

    go(diffs)
  }
}
