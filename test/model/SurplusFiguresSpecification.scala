package model

import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen, Properties}

object SurplusFiguresSpecification extends Properties("SurplusFigures") {

  val arbInt = Arbitrary { Gen.choose(-1000000, 1000000) }.arbitrary
  val posInt = Arbitrary { Gen.choose(0, 1000000) }.arbitrary
  val negInt = Arbitrary { Gen.choose(-1000000, 0) }.arbitrary

  val genAmount: Gen[Amount] = for (pence <- arbInt) yield Amount(pence)
  val genPosAmount: Gen[Amount] = for (pence <- posInt) yield Amount(pence)
  val genNegAmount: Gen[Amount] = for (pence <- negInt) yield Amount(pence)

  implicit lazy val arbSurplusFigures: Arbitrary[SurplusFigures] = Arbitrary {
    for {
      income <- genPosAmount
      spend <- genNegAmount
      repayments <- genNegAmount
      refunds <- genPosAmount
      uncategorised <- genAmount
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
    figures.totalSpend.isNeg
  }
}
