package controllers

import java.time.LocalDate

import com.google.api.client.auth.oauth2.Credential
import model._
import services.{GoogleSheet, Row, Sheet}
import util.Util.asOption

object Helper {

  private val accountSheet = Sheet("Accounts", numCols = 3)

  val transactionSheet = Sheet("Transactions", numCols = 7)

  private def toAccount(r: Row): Account = Account(
    name = r.head,
    accType = AccountType.fromName(r(1)),
    originalBalance = Amount.fromString(r(2))
  )

  private def abs(amountText: String) = amountText.stripPrefix("-")

  private def toTransaction(r: Row) = Transaction(
    account = r.head,
    date = LocalDate.parse(r(1)),
    payee = r(2),
    reference = asOption(r(3)),
    mode = asOption(r(4)),
    amount = Amount.fromString(r(5)),
    category = Category.fromCode(r(6))
  )

  def allAccounts(implicit userId: String) =
    GoogleSheet.allRows(accountSheet).map(toAccount)

  def allTransactions(implicit userId: String) =
    GoogleSheet.allRows(transactionSheet).map(toTransaction)

  def allTransactions2(credential: Credential) =
    GoogleSheet.allRows2(transactionSheet, credential).map(toTransaction)
}
