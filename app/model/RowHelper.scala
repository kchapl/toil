package model

import java.time.LocalDate

object RowHelper {

  def toAccount(values: Seq[String]): Account = {
    //noinspection ZeroIndexToHead
    Account(
      name = values(0),
      originalBalance = Amount.fromString(values(1)),
      transactions = Set.empty
    )
  }

  def toTransaction(values: Seq[String]): Transaction = {
    def opt(s: String) = if (s.isEmpty) None else Some(s)
    //noinspection ZeroIndexToHead
    Transaction(
      account = values(0),
      date = LocalDate.parse(values(1)),
      payee = values(2),
      reference = opt(values(3)),
      mode = opt(values(4)),
      amount = Amount.fromString(values(5)),
      category = values(6)
    )
  }

  def toRow(tx: Transaction) = Seq(
    tx.account,
    tx.date.toString,
    tx.payee,
    tx.reference getOrElse "",
    tx.mode getOrElse "",
    tx.amount.pounds.toString
  )
}
