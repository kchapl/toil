package model

import java.time.Month

import util.Util.sequence

case class MonthSurplus(
    year: Int,
    month: Month,
    income: Amount,
    spend: Amount,
    repayments: Amount,
    refunds: Amount
) {
  val totalSpend: Amount = spend plus repayments minus refunds
  val surplus: Amount    = income minus totalSpend
}

object Surplus {

  def fromTransactions(
      transactions: Set[Transaction]
  ): Either[String, Seq[MonthSurplus]] =
    for (months <- new SurplusCalculator(transactions).months.right)
      yield months.sortBy(s => (s.year, s.month))
}

case class Surplus(
    income: Amount,
    spend: Amount,
    repayments: Amount,
    refunds: Amount
) {
  val surplus: Amount = income minus spend
}

class SurplusCalculator(transactions: Set[Transaction]) {

  lazy val months: Either[String, Seq[MonthSurplus]] = {
    val txDates = transactions.view.map(_.date)
    sequence {
      DateRange(txDates.minBy(_.toEpochDay), txDates.maxBy(_.toEpochDay)).months map { m =>
        surplus(m) match {
          case Left(msg) => Left(msg)
          case Right(s) =>
            val d = m.from
            Right(
              MonthSurplus(
                d.getYear,
                d.getMonth,
                s.income,
                s.spend,
                s.repayments,
                s.refunds
              )
            )
        }
      }
    }
  }

  def inDateRange(r: DateRange): Set[Transaction] =
    for (t <- transactions if r.contains(t.date)) yield t

  def surplus(r: DateRange): Either[String, Surplus] = {

    def sum(p: Transaction => Boolean): Amount =
      inDateRange(r).foldLeft(Amount(0)) {
        case (soFar, t) =>
          if (p(t)) soFar plus t.amount else soFar
      }

    if (transactions.exists(_.isUncategorised))
      Left("Uncategorised transaction")
    else
      Right(
        Surplus(
          income = sum(_.isIncome),
          spend = sum(_.isSpend).neg,
          repayments = sum(_.isRepayment).neg,
          refunds = sum(_.isRefund)
        )
      )
  }
}
