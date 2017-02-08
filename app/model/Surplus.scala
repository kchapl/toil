package model

import java.time.Month

case class MonthSurplus(
  year: Int,
  month: Month,
  income: Amount,
  spend: Amount
) {
  val surplus: Amount = income minus spend
}

object Surplus {

  def fromTransactions(transactions: Set[Transaction]): Seq[MonthSurplus] =
    new SurplusCalculator(transactions).months.sortBy(s => (s.year, s.month))
}

case class Surplus(income: Amount, spend: Amount) {
  val surplus: Amount = income minus spend
}

class SurplusCalculator(transactions: Set[Transaction]) {

  lazy val months: Seq[MonthSurplus] = {
    val txDates = transactions.view.map(_.date)
    DateRange(txDates.minBy(_.toEpochDay), txDates.maxBy(_.toEpochDay)).months map { m =>
      val s = surplus(m)
      val d = m.from
      MonthSurplus(d.getYear, d.getMonth, s.income, s.spend)
    }
  }

  def inDateRange(r: DateRange): Set[Transaction] =
    for (t <- transactions if r.contains(t.date)) yield t

  def surplus(r: DateRange): Surplus = {

    def sum(p: Transaction => Boolean): Amount =
      inDateRange(r).foldLeft(Amount(0)) { case (soFar, t) =>
        if (p(t)) soFar plus t.amount else soFar
      }

    Surplus(
      income = sum(_.isIncome),
      spend = sum(t => t.isSpend || t.isRepayment || t.isRefund).neg
    )
  }
}
