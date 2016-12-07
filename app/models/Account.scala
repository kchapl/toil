package models

import java.time.LocalDate

import play.api.libs.ws.WSClient
import services.GoogleSheet

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Account(name: String, originalBalance: Balance, latestBalance: Balance)

object Account {

  def fromRow(r: Row) = {
    val balance = Balance(
      date = LocalDate.parse(r.values(1)),
      amount = Amount(r.values(2).toDouble)
    )
    Account(
      name = r.values(0),
      originalBalance = balance,
      latestBalance = balance
    )
  }

  def fetchAll(ws: WSClient, accessToken: String): Future[Seq[Account]] = {
    GoogleSheet.getValues(
      ws, accessToken, Config.sheetFileId.get, SheetRange("Accounts", "A", "F")
    ) flatMap {
      case Left(msg) =>
        Future.successful(Nil)
      case Right(rows) =>
        val as = rows map { r =>
          val init = fromRow(r)
          Transaction.fetchForAccount(ws, accessToken, init.name) map { txs =>
            txs.lastOption map { tx =>
              val latestBalance = Balance(
                date = tx.date,
                amount = Amount.sum(init.originalBalance.amount +: txs.map(_.amount))
              )
              init.copy(latestBalance = latestBalance)
            } getOrElse init
          }
        }
        Future.sequence(as)
    }
  }
}
