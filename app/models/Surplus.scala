package models

import java.time.Month

case class Surplus(
  year: Int,
  month: Month,
  income: Double,
  spend: Double,
  transfers: Double
) {
  val surplus = income - spend
}

object Surplus {
  def fromTransactions(year: Int, month: Month, transactions: Seq[Transaction]) = {
    def sum(p: Transaction => Boolean) = transactions.filter(p).map(_.amount).sum
    Surplus(
      year,
      month,
      income = sum(_.isIncome),
      spend = sum(_.isSpend),
      transfers = sum(_.isTransfer)
    )
  }
}
