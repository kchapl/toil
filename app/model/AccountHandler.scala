package model

import model.RowHelper.toAccount
import model.TransactionHandler.allTransactions

object AccountHandler {

  private val accountSheet = Sheet("Accounts", numCols = 2)

  def allAccounts(userId: String)(fetch: (String, Sheet) => Seq[Seq[String]]): Set[Account] = {
    def fillTransactions(rawAccounts: Seq[Account]): Set[Account] = {
      rawAccounts.map { a =>
        a.copy(transactions = allTransactions(userId)(fetch).filter(_.account == a.name))
      }.toSet
    }
    val rows = fetch(userId, accountSheet)
    val accounts = rows.map(toAccount)
    fillTransactions(accounts)
  }

  def account(name: String, userId: String)
    (fetch: (String, Sheet) => Seq[Seq[String]]): Option[Account] =
    allAccounts(userId)(fetch) find (_.name.toLowerCase == name.toLowerCase)
}
