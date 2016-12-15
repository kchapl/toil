package services

import java.time.LocalDate

import models.{Account, Amount, Transaction}
import services.GoogleSheet.Row

object RowAdaptor {

  def toAccount(r: Row) = Account(
    name = r.values(0),
    originalBalance = Amount(r.values(1).toDouble),
    transactions = Set.empty
  )

  def toTransaction(row: Row) = {
    def opt(s: String) = if (s.isEmpty) None else Some(s)
    Transaction(
      account = row.values(0),
      date = LocalDate.parse(row.values(1)),
      payee = row.values(2),
      reference = opt(row.values(3)),
      mode = opt(row.values(4)),
      amount = Amount(row.values(5))
    )
  }

  def toRow(tx: Transaction) = Row(
    Seq(
      tx.account,
      tx.date.toString,
      tx.payee,
      tx.reference getOrElse "",
      tx.mode getOrElse "",
      tx.amount.toString
    )
  )
}
