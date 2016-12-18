package services

import javax.inject.Inject

import model.{Account, Config, Transaction}
import play.api.Logger
import play.api.libs.ws.WSClient
import services.GoogleSheet.SheetRange
import services.RowAdaptor._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GoogleSheetRepository @Inject()(ws: WSClient) extends Repository {

  def fetchAllTransactions(accessToken: String): Future[Set[Transaction]] = {

    Logger.info("Fetching all transactions ...")

    GoogleSheet.getValues(
      ws, accessToken, Config.sheetFileId.get, SheetRange("Transactions", "A", "F")
    ) map {
      case Left(msg) => Set.empty
      case Right(rows) => rows.map(toTransaction).toSet
    }
  }

  def insertTransactions(accessToken: String, transactions: Set[Transaction]): Unit = {

    Logger.info(s"Inserting ${ transactions.size } transactions ...")

    fetchAllTransactions(accessToken) foreach { already =>
      val newTxs = transactions -- already
      GoogleSheet.appendValues(
        ws, accessToken, Config.sheetFileId.get, SheetRange("Transactions", "A", "F"),
        values = newTxs.map(toRow).toSeq
      )
    }
  }

  def fetchAllAccounts(accessToken: String): Future[Set[Account]] = {

    Logger.info("Fetching all accounts ...")

    GoogleSheet.getValues(
      ws, accessToken, Config.sheetFileId.get, SheetRange("Accounts", "A", "F")
    ) flatMap {
      case Left(msg) =>
        Future.successful(Set.empty)
      case Right(rows) =>
        val inits = rows map toAccount
        val eventualTxs = fetchAllTransactions(accessToken)
        eventualTxs map { txs =>
          inits.map { init =>
            val accTxs = txs.filter(_.account == init.name)
            init.copy(transactions = accTxs)
          }.toSet
        }
    }
  }
}
