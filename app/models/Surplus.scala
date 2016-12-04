package models

import java.time.Month

import models.Amount.abs

case class Surplus(
  year: Int,
  month: Month,
  income: Amount,
  spend: Amount
) {
  val surplus = income minus spend
}

object Surplus {
  def fromTransactions(year: Int, month: Month, transactions: Seq[Transaction]) = {
    def sum(p: Transaction => Boolean): Amount =
      Amount.sum(transactions.filter(p).map(t => abs(t.amount)))
    Surplus(
      year,
      month,
      income = sum(_.isIncome),
      spend = sum(_.isSpend)
    )
  }
}
