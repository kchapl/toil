package model

import cats.Monoid.combineAll

case class SurplusFigures(
    income: Amount,
    spend: Amount,
    repayments: Amount,
    refunds: Amount,
    uncategorised: Amount
) {
  val totalSpend: Amount = spend plus repayments minus refunds
  val surplus: Amount    = income minus totalSpend
}

object SurplusFigures {

  def fromTransactions(ts: Seq[Transaction]): SurplusFigures = {

    def sum(p: Transaction => Boolean) = combineAll(ts.filter(p).map(_.amount.abs))

    SurplusFigures(
      income = sum(_.isIncome),
      spend = sum(_.isSpend),
      repayments = sum(_.isRepayment),
      refunds = sum(_.isRefund),
      uncategorised = sum(_.isUncategorised)
    )
  }
}
