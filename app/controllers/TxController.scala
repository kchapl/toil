package controllers

import javax.inject.Inject

import models.{Config, Transaction}
import play.api.libs.ws.WSClient
import play.api.mvc.Controller
import services.GoogleSheet

import scala.concurrent.ExecutionContext

class TxController @Inject()(ws: WSClient)(implicit context: ExecutionContext)
  extends Controller with Security {

  def viewTransactions() = AuthorizedAction.async { implicit request =>
    GoogleSheet.getValues(
      ws,
      request.accessToken,
      Config.sheetFileId.get,
      "a1:f5"
    ) map {
      case Left(e) =>
        InternalServerError(s"${ e.code }: ${ e.description }")
      case Right(rows) =>
        val transactions = rows map Transaction.fromRow
        Ok(views.html.transactions(transactions))
    }
  }

  //  def uploadTransactions() = AuthorizedAction { implicit request =>
  //    txPath map { path =>
  //      val transactions = Source.fromFile(path).getLines().toSeq map
  // TxParser.parseLine
  //
  //      GoogleSheet.appendValues(ws, request.accessToken, Config
  // .sheetFileId.get, range = "", values = Nil)
  //
  //      Redirect(routes.TxController.viewTransactions())
  //    } getOrElse {
  //      InternalServerError("Missing property 'TX_PATH'")
  //    }
  //  }
}
