package controllers

import javax.inject.Inject

import models.{Config, Transaction, TxParser}
import play.api.libs.ws.WSClient
import play.api.mvc.Controller
import services.GoogleSheet

import scala.concurrent.ExecutionContext
import scala.io.Source

class TxController @Inject()(ws: WSClient)(implicit context: ExecutionContext)
  extends Controller with Security {

  def viewTransactions() = AuthorizedAction.async { implicit request =>
    GoogleSheet.getValues(
      ws,
      request.accessToken,
      Config.sheetFileId.get,
      "a1:f5"
    ) map {
      case Left(msg) =>
        InternalServerError(s"$msg")
      case Right(rows) =>
        val transactions = rows map Transaction.fromRow
        Ok(views.html.transactions(transactions))
    }
  }

  def uploadTransactions() = AuthorizedAction.async { implicit request =>

    val transactions =
      Source.fromFile(Config.txPath.get).getLines().toSeq map
        TxParser.parseLine("acc")

    val x = GoogleSheet.appendValues(
      ws,
      request.accessToken,
      Config.sheetFileId.get,
      range = "a1:f5",
      values = transactions map Transaction.toRow
    )

    x map {
      case Left(msg) =>
        InternalServerError(s"$msg")
      case Right(_) =>
        Redirect(routes.TxController.viewTransactions())
    }
  }
}
