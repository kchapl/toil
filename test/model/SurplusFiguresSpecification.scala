package model

import model.AmountGenerator._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop.forAll
import org.scalacheck.Shrink.shrink
import org.scalacheck.{Arbitrary, Properties, Shrink}

object SurplusFiguresSpecification extends Properties("SurplusFigures") {

  implicit lazy val arbSurplusFigures: Arbitrary[SurplusFigures] = Arbitrary {
    for {
      income <- arbitrary[Amount]
      spend <- arbitrary[Amount]
      repayments <- arbitrary[Amount]
      refunds <- arbitrary[Amount]
      uncategorised <- arbitrary[Amount]
    } yield
      SurplusFigures(
        income,
        spend,
        repayments,
        refunds,
        uncategorised
      )
  }

  implicit def shrinkSurplusFigures(implicit s: Shrink[SurplusFigures]): Shrink[SurplusFigures] = Shrink { figures =>
    for {
      income <- shrink(figures.income)
      spend <- shrink(figures.spend)
      repayments <- shrink(figures.repayments)
      refunds <- shrink(figures.refunds)
      uncategorised <- shrink(figures.uncategorised)
    } yield
      SurplusFigures(
        income,
        spend,
        repayments,
        refunds,
        uncategorised
      )
  }

  property("totalSpend") = forAll { figures: SurplusFigures =>
    figures.totalSpend != Amount.zero
  }
}
