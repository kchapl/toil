package model

import model.RowHelper.toAccount
import model.TransactionHandler.allTransactions

object AccountHandler {

  private val accountSheet = Sheet("Accounts", numCols = 2)

  def allAccounts(accessToken: String)(fetch: (String, Sheet) => Seq[Seq[String]]): Set[Account] = {
    def fillTransactions(rawAccounts: Seq[Account]): Set[Account] = {
      rawAccounts.map { a =>
        a.copy(transactions = allTransactions(accessToken)(fetch).filter(_.account == a.name))
      }.toSet
    }
    val rows = fetch(accessToken, accountSheet)
    val accounts = rows.map(toAccount)
    fillTransactions(accounts)
  }

  def account(name: String, accessToken: String)
    (fetch: (String, Sheet) => Seq[Seq[String]]): Option[Account] =
    allAccounts(accessToken)(fetch) find (_.name.toLowerCase == name.toLowerCase)
}
