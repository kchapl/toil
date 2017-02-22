package controllers

import java.time.LocalDate

import model.{Account, AccountType, Amount, Transaction}
import services.{GoogleSheet, Row, Sheet}
import util.Util.opt

object Helper {

  private val accountSheet = Sheet("Accounts", numCols = 2)

  val transactionSheet = Sheet("Transactions", numCols = 7)

  private def toAccount(r: Row): Account = Account(
    name = r.head,
    originalBalance = Amount.fromString(r(1)),
    accType = AccountType.fromName(r(2))
  )

  private def toTransaction(r: Row) = Transaction(
    account = r.head,
    date = LocalDate.parse(r(1)),
    payee = r(2),
    reference = opt(r(3)),
    mode = opt(r(4)),
    amount = Amount.fromString(r(5)),
    category = r(6)
  )

  def allAccounts(implicit userId: String) =
    GoogleSheet.allRows(accountSheet).map(toAccount)

  def allTransactions(implicit userId: String) =
    GoogleSheet.allRows(transactionSheet).map(toTransaction)
}
