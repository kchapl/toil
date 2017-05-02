package model

import model.TransactionGenerator._
import org.scalacheck.Prop._
import org.scalacheck.{Prop, Properties}

object SurplusesSpecification extends Properties("Surpluses") {

  private def monthlyAmounts(transactions: Seq[Transaction], amount: SurplusFigures => Amount) = {
    Surpluses.fromTransactions(transactions).monthly.foldLeft(Amount.zero) { (acc, surplus) =>
      Amount.sum(Seq(acc, amount(surplus.figures)))
    }
  }

  private def seasonalAmounts(transactions: Seq[Transaction], amount: SurplusFigures => Amount) = {
    Surpluses.fromTransactions(transactions).seasonal.foldLeft(Amount.zero) { (acc, surplus) =>
      Amount.sum(Seq(acc, amount(surplus.figures)))
    }
  }

  private def totalIncome(transactions: Seq[Transaction]) =
    Amount.sum(transactions.filter(_.isIncome).map(_.amount.abs))

  private def grandTotalSpend(transactions: Seq[Transaction]) = {
    val totalSpend      = Amount.sum(transactions.filter(_.isSpend).map(_.amount.abs))
    val totalRepayments = Amount.sum(transactions.filter(_.isRepayment).map(_.amount.abs))
    val totalRefunds    = Amount.sum(transactions.filter(_.isRefund).map(_.amount.abs))
    totalSpend.plus(totalRepayments).minus(totalRefunds)
  }

  property("fromTransactions") = {

    val propMonthlyIncome = forAll { transactions: Seq[Transaction] =>
      val totalIncomeAccordingToSurpluses = monthlyAmounts(transactions, { _.income })
      s"Calculated: $totalIncomeAccordingToSurpluses" |: Prop(
        totalIncomeAccordingToSurpluses == totalIncome(transactions))
    }

    val propSeasonalIncome = forAll { transactions: Seq[Transaction] =>
      val totalIncomeAccordingToSurpluses = seasonalAmounts(transactions, { _.income })
      s"Calculated: $totalIncomeAccordingToSurpluses" |: Prop(
        totalIncomeAccordingToSurpluses == totalIncome(transactions))
    }

    val propMonthlyTotalSpend = forAll { transactions: Seq[Transaction] =>
      val grandTotalSpendAccordingToSurpluses = monthlyAmounts(transactions, { _.totalSpend })
      s"Calculated: $grandTotalSpendAccordingToSurpluses" |: Prop(
        grandTotalSpendAccordingToSurpluses == grandTotalSpend(transactions))
    }

    val propSeasonalTotalSpend = forAll { transactions: Seq[Transaction] =>
      val grandTotalSpendAccordingToSurpluses = seasonalAmounts(transactions, { _.totalSpend })
      grandTotalSpendAccordingToSurpluses == grandTotalSpend(transactions)
    }

    all(
      "Monthly income" |: propMonthlyIncome,
      "Seasonal income" |: propSeasonalIncome,
      "Monthly total spend" |: propMonthlyTotalSpend,
      "Seasonal total spend" |: propSeasonalTotalSpend
    )
  }
}
