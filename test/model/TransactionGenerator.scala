package model

import model.time.DateGenerator._
import org.scalacheck.{Arbitrary, Gen}

object TransactionGenerator {

  private val genCategory: Gen[Category] = Gen.oneOf(Seq(Income, Spend, Transfer, Repayment, Refund, Uncategorised))

  private val genTransaction: Gen[Transaction] =
    for {
      account <- Gen.alphaStr
      date <- genDateIn21stCentury
      payee <- Gen.alphaStr
      reference <- Gen.option(Gen.alphaStr)
      mode <- Gen.option(Gen.alphaStr)
      pence <- Gen.chooseNum(0, 1000000)
      category <- genCategory
    } yield
      Transaction(
        account,
        date,
        payee,
        reference,
        mode,
        Amount(pence),
        category
      )

  implicit lazy val arbTransaction = Arbitrary(genTransaction)
}
