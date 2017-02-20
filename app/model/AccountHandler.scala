package model

import model.RowHelper.toAccount
import model.TransactionHandler.allTransactions

object AccountHandler {

  private val accountSheet = Sheet("Accounts", numCols = 2)

  def allAccounts(fetch: Sheet => Seq[Seq[String]]): Set[Account] = {
    def fillTransactions(rawAccounts: Seq[Account]): Set[Account] = {
      rawAccounts.map { a =>
        val transactions = allTransactions(fetch)
        a.copy(transactions = transactions.filter(_.account == a.name))
      }.toSet
    }
    val rows = fetch(accountSheet)
    val accounts = rows.map(toAccount)
    fillTransactions(accounts)
  }

  def account(name: String)(fetch: Sheet => Seq[Seq[String]]): Option[Account] =
    allAccounts(fetch) find (_.name.toLowerCase == name.toLowerCase)
}
