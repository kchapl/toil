package model

import model.RowHelper._

import scala.io.BufferedSource

object TransactionHandler {

  private val transactionSheet = Sheet("Transactions", numCols = 7)

  def allTransactions(accessToken: String)
    (fetch: (String, Sheet) => Seq[Seq[String]]): Set[Transaction] = {
    val rows = fetch(accessToken, transactionSheet)
    rows.map(toTransaction).toSet
  }

  def uploadTransactions(accessToken: String, accountName: String, src: BufferedSource)
    (fetch: (String, Sheet) => Seq[Seq[String]])
    (append: (String, Sheet, Seq[Seq[String]]) => Int): Int = {
    val txToAppend = {
      def parse(line: String) = accountName match {
        case "HongCurr" => TransactionParser.parseHongCurrLine(accountName)(line)
        case "HongCredit" => TransactionParser.parseHongCreditLine(accountName)(line)
      }
      src.getLines().toSet map parse
    }
    val newTxs = txToAppend -- allTransactions(accessToken)(fetch)
    append(accessToken, transactionSheet, newTxs.map(toRow).toSeq)
  }
}
