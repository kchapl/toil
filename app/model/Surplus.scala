package model

import java.time.Month

case class Surplus(
  year: Int,
  month: Month,
  income: Amount,
  spend: Amount
) {
  val surplus = income minus spend
}

object Surplus {

  def fromTransactions(transactions: Set[Transaction]): Seq[Surplus] = {

    def fromTransactions(year: Int, month: Month, transactions: Set[Transaction]): Surplus = {
      def sum(p: Transaction => Boolean): Amount =
        Amount.sum(transactions.filter(p).map(t => Amount.abs(t.amount)).toSeq:_*)
      Surplus(
        year,
        month,
        income = sum(_.isIncome),
        spend = sum(_.isSpend)
      )
    }

    transactions.groupBy { t =>
      (t.date.getYear, t.date.getMonth)
    }.map {
      case ((year, month), ts) => fromTransactions(year, month, ts)
    }.toSeq.sortBy(s => (s.year, s.month))
  }
}
