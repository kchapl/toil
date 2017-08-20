package controllers

import java.time.LocalDate

import com.google.api.client.auth.oauth2.Credential
import model._
import services.{GoogleSheet, Row, Sheet}
import util.Util.asOption

object Helper {

  val transactionSheet = Sheet("Transactions", numCols = 7)

  def toAccount(r: Row): Account = Account(
    name = r.head,
    accType = AccountType.fromName(r(1)),
    originalBalance = Amount.fromString(r(2))
  )

  def toTransaction(r: Row) = Transaction(
    account = r.head,
    date = LocalDate.parse(r(1)),
    payee = r(2),
    reference = asOption(r(3)),
    mode = asOption(r(4)),
    amount = Amount.fromString(r(5)),
    category = Category.fromCode(r(6))
  )

  def allTransactions(credential: Credential) =
    GoogleSheet.allRows(transactionSheet, credential).map(toTransaction)
}
