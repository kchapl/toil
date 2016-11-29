package models

import java.time.LocalDate

import play.api.libs.ws.WSClient
import services.GoogleSheet

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Transaction(
  account: String,
  date: LocalDate,
  payee: String,
  reference: Option[String],
  mode: Option[String],
  amount: Amount
) {
  val isTransfer = mode.contains("TFR") || payee.contains("LOAN") || payee.contains("MONEY")
  val isTransferFrom = isTransfer && amount.isPos
  val isTransferTo = isTransfer && amount.isNeg
  val isIncome = amount.isPos && !isTransfer
  val isSpend = amount.isNeg && !isTransfer
}

object Transaction {

  def fromRow(row: Row) = {
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

  def fetchAll(ws: WSClient, accessToken: String): Future[Either[String, Seq[Transaction]]] = {
    GoogleSheet.getValues(
      ws,
      accessToken,
      Config.sheetFileId.get,
      SheetRange("Transactions", "A", "F")
    ) map {
      case Left(msg) => Left(msg)
      case Right(rows) =>
        Right(rows.map(Transaction.fromRow))
    }
  }

  def append(txToAppend: Set[Transaction], txAlready: Set[Transaction])
    (f: Set[Transaction] => Future[Either[String, Unit]]): Future[Either[String, Unit]] = {
    f(txToAppend -- txAlready)
  }
}
