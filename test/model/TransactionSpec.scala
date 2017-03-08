package model

import java.time.LocalDate

import org.scalatestplus.play._

import scala.io.Source

class TransactionSpec extends PlaySpec {

  private val currentCsv = """08/03/2017,P       MISCELLANEOUS            BP,"-23.00"
                             |07/03/2017,P       MISCELLANEOUS            BP,"-25.00"""".stripMargin

  private val creditCsv = ""

  private val currentAccount = Account(
    name = "curr",
    accType = Current,
    originalBalance = Amount(0)
  )

  private val creditAccount = Account(
    name = "cred",
    accType = Credit,
    originalBalance = Amount(0)
  )

  "toImport" must {
    "give all new transactions to import from a current account" in {
      Transaction.toImport(
        before = Set(
          Transaction(
            account = "curr",
            date = LocalDate.of(2017, 3, 8),
            payee = "p",
            reference = None,
            mode = None,
            amount = Amount(1),
            category = "c"
          )
        ),
        accounts = Set(currentAccount, creditAccount),
        accountName = "curr",
        source = Source.fromString(currentCsv)
      ) mustBe Right(
        Set(
          Transaction(
            account = "curr",
            date = LocalDate.of(2017, 3, 8),
            payee = "P",
            reference = Some("MISCELLANEOUS"),
            mode = Some("BP"),
            amount = Amount(-2300),
            category = "U"
          ),
          Transaction(
            account = "curr",
            date = LocalDate.of(2017, 3, 7),
            payee = "P",
            reference = Some("MISCELLANEOUS"),
            mode = Some("BP"),
            amount = Amount(-2500),
            category = "U"
          )
        ))
    }
  }

  "give transactions not already present to import from a current account" in {
    Transaction.toImport(
      before = Set(
        Transaction(
          account = "curr",
          date = LocalDate.of(2017, 3, 7),
          payee = "P",
          reference = Some("MISCELLANEOUS"),
          mode = Some("BP"),
          amount = Amount(-2500),
          category = "c"
        )
      ),
      accounts = Set(currentAccount, creditAccount),
      accountName = "curr",
      source = Source.fromString(currentCsv)
    ) mustBe Right(
      Set(
        Transaction(
          account = "curr",
          date = LocalDate.of(2017, 3, 8),
          payee = "P",
          reference = Some("MISCELLANEOUS"),
          mode = Some("BP"),
          amount = Amount(-2300),
          category = "U"
        )
      ))
  }

  "give all new transactions to import from a credit account" in {
    Transaction.toImport(
      before = Set(
        Transaction(
          account = "curr",
          date = LocalDate.of(2017, 3, 7),
          payee = "P",
          reference = Some("MISCELLANEOUS"),
          mode = Some("BP"),
          amount = Amount(-2500),
          category = "c"
        )
      ),
      accounts = Set(currentAccount, creditAccount),
      accountName = "cred",
      source = Source.fromString(creditCsv)
    ) mustBe Right(
      Set(
        Transaction(
          account = "cred",
          date = LocalDate.of(2017, 3, 8),
          payee = "P",
          reference = Some("MISCELLANEOUS"),
          mode = None,
          amount = Amount(-2300),
          category = "U"
        )
      ))
  }
}
