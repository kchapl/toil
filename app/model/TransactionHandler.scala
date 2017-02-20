package model

import model.RowHelper._
import model.Transaction.transactionSheet

import scala.io.BufferedSource

object TransactionHandler {

  def allTransactions(fetch: Sheet => Seq[Seq[String]]): Set[Transaction] = {
    val rows = fetch(transactionSheet)
    rows.map(Transaction.fromRow).toSet
  }

  def uploadTransactions(accountName: String, src: BufferedSource)
    (fetch: Sheet => Seq[Seq[String]])
    (append: (Sheet, Seq[Seq[String]]) => Int): Int = {
    val txToAppend = {
      def parse(line: String) = accountName match {
        case "HongCurr" => TransactionParser.parseHongCurrLine(accountName)(line)
        case "HongCredit" => TransactionParser.parseHongCreditLine(accountName)(line)
      }
      src.getLines().toSet map parse
    }
    val transactions = allTransactions(fetch)
    val newTxs = txToAppend -- transactions
    append(
      transactionSheet,
      for (t <- newTxs.toSeq) yield toRow(t)
    )
  }
}
