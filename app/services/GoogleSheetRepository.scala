package services

import javax.inject.Inject

import models._
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GoogleSheetRepository @Inject()(ws: WSClient) extends Repository {

  private def rowToAccount(r: Row) = Account(
    name = r.values(0),
    originalBalance = Amount(r.values(1).toDouble),
    transactions = Nil
  )

  def fetchAllAccounts(accessToken: String): Future[Seq[Account]] = {
    GoogleSheet.getValues(
      ws, accessToken, Config.sheetFileId.get, SheetRange("Accounts", "A", "F")
    ) flatMap {
      case Left(msg) =>
        Future.successful(Nil)
      case Right(rows) =>
        val as = rows map { r =>
          val init = rowToAccount(r)
          Transaction.fetchForAccount(ws, accessToken, init.name) map { txs =>
            init.copy(transactions = txs)
          }
        }
        Future.sequence(as)
    }
  }
}
