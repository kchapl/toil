package model

import java.time.LocalDate

case class DateAmount(date: LocalDate, amount: Amount)

object DateAmount {

  def fromTransactions(transactions: Set[Transaction]): Seq[DateAmount] = {
    transactions.groupBy(_.date).map {
      case (date, txs) => DateAmount(date, Amount.sum(txs.toSeq.map(_.amount)))
    }.toSeq.sortBy(_.date.toEpochDay)
  }
}
