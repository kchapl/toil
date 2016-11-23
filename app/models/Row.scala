package models

case class Row(values: Seq[String])

object Row {

  def fromTransaction(tx: Transaction) = Row(
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
