package model

import org.scalacheck.Prop.forAll
import org.scalacheck.Shrink.shrink
import org.scalacheck.{Arbitrary, Gen, Shrink}

object AmountGenerator {

  private val genPosAmount: Gen[Amount] = for (pence <- Gen.choose(1, 1000000)) yield Amount(pence)

  implicit lazy val arbAmount: Arbitrary[Amount] = Arbitrary(genPosAmount)

  implicit def shrinkAmount(implicit s: Shrink[Amount]): Shrink[Amount] = Shrink { amount =>
    for (pence <- shrink(amount.pence)) yield Amount(pence)
  }
}
