package model

import java.time.LocalDate

import model.AmountGenerator._
import model.time.DateGenerator._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

object TransactionGenerator {

  private val genCategory: Gen[Category] = Gen.oneOf(Seq(Income, Spend, Transfer, Repayment, Refund, Uncategorised))

  private val genTransaction: Gen[Transaction] =
    for {
      account <- Gen.alphaStr
      date <- arbitrary[LocalDate]
      payee <- Gen.alphaStr
      reference <- Gen.option(Gen.alphaStr)
      mode <- Gen.option(Gen.alphaStr)
      amount <- arbitrary[Amount]
      category <- genCategory
    } yield
      Transaction(
        account,
        date,
        payee,
        reference,
        mode,
        amount,
        category
      )

  implicit lazy val arbTransaction = Arbitrary(genTransaction)
}
