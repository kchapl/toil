package models

import java.time.LocalDate

import services.GoogleSheet
import services.GoogleSheet.Row

import scala.concurrent.Future

case class Transaction(
  account: String,
  date: LocalDate,
  payee: String,
  reference: Option[String],
  mode: String,
  amount: Double
)

object Transaction {

  def fromRow(row: GoogleSheet.Row) = Transaction(
    account = row.values(0),
    date = LocalDate.parse(row.values(1)),
    payee = row.values(2),
    reference = if (row.values(3).isEmpty) None else Some(row.values(3)),
    mode = row.values(4),
    amount = row.values(5).toDouble
  )

  def toRow(tx: Transaction) = Row(
    Seq(
      tx.account,
      tx.date.toString,
      tx.payee,
      tx.reference getOrElse "",
      tx.mode,
      tx.amount.toString
    )
  )

  def append(): Future[Either[String, Unit]] = ???
}
