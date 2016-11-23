package models

import java.time.LocalDate

import scala.concurrent.Future

case class Transaction(
  account: String,
  date: LocalDate,
  payee: String,
  reference: Option[String],
  mode: Option[String],
  amount: Double
)

object Transaction {

  def fromRow(row: Row) = {
    def opt(s: String) = if (s.isEmpty) None else Some(s)
    Transaction(
      account = row.values(0),
      date = LocalDate.parse(row.values(1)),
      payee = row.values(2),
      reference = opt(row.values(3)),
      mode = opt(row.values(4)),
      amount = row.values(5).toDouble
    )
  }

  def append(txToAppend: Set[Transaction], txAlready: Set[Transaction])
    (f: Set[Transaction] => Future[Either[String, Unit]]): Future[Either[String, Unit]] = {
    f(txToAppend -- txAlready)
  }
}
