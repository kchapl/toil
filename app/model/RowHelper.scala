package model

object RowHelper {

  def toAccount(values: Seq[String]): Account = {
    Account(
      name = values.head,
      originalBalance = Amount.fromString(values(1)),
      transactions = Set.empty
    )
  }

  def toRow(tx: Transaction) = Seq(
    tx.account,
    tx.date.toString,
    tx.payee,
    tx.reference getOrElse "",
    tx.mode getOrElse "",
    tx.amount.pounds.toString,
    tx.category
  )
}
