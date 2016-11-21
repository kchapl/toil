package controllers

import javax.inject.Inject

import models._
import play.api.libs.ws.WSClient
import play.api.mvc.Controller
import services.GoogleSheet

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

class TxController @Inject()(ws: WSClient)(implicit context: ExecutionContext)
  extends Controller with Security {

  def viewTransactions = AuthorizedAction.async { implicit request =>
    GoogleSheet.getValues(
      ws,
      request.accessToken,
      Config.sheetFileId.get,
      SheetRange("Transactions", "A", "F")
    ) map {
      case Left(msg) =>
        InternalServerError(s"$msg")
      case Right(rows) =>
        val transactions = rows map Transaction.fromRow
        Ok(views.html.transactions(transactions))
    }
  }

  def uploadTransactions = AuthorizedAction.async(parse.multipartFormData) { implicit request =>
    request.body.file("transactions").map { transactions =>

      val acc = request.body.dataParts("account").head

      val transactionsToAppend =
        Source.fromFile(transactions.ref.file).getLines().toSet map
          TxParser.parseLine(acc)

      val transactionsAlready = GoogleSheet.getValues(
        ws,
        request.accessToken,
        Config.sheetFileId.get,
        SheetRange("Transactions", "A", "F")
      )

      val x = transactionsAlready map {
        case Left(msg) =>
          val w = Left(Future.successful(msg))
          w
        case Right(r) =>
          val deduped = transactionsToAppend -- (r map Transaction.fromRow)

          val y = GoogleSheet.appendValues(
            ws,
            request.accessToken,
            Config.sheetFileId.get,
            range = SheetRange("Transactions", "A", "F"),
            values = (deduped map Row.fromTransaction).toSeq
          )
          val z = Right(y)
          z
      }

      x map {
        case Left(msg) =>
          InternalServerError(s"$msg")
        case Right(_) =>
          Redirect(routes.TxController.viewTransactions())
      }
    } getOrElse {
      Future.successful(Ok("File upload failed"))
    }
  }

  def viewUploadTransactions() = AuthorizedAction {
    Ok(views.html.transactionsUpload())
  }
}
