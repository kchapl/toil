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
        val transactions = Organizer.organize(rows.map(Transaction.fromRow), request.queryString)
        Ok(views.html.transactions(transactions))
    }
  }

  def uploadTransactions = AuthorizedAction.async(parse.multipartFormData) { implicit request =>
    request.body.file("transactions").map { transactions =>

      val range = SheetRange("Transactions", fromColumn = "A", toColumn = "F")

      def appended(txAlready: Set[Transaction]): Future[Either[String, Unit]] = {

        val acc = request.body.dataParts("account").head

        val txToAppend = {
          def parse(line: String) = acc match {
            case "HongCurr" => TxParser.parseHongCurrLine(acc)(line)
            case "HongCredit" => TxParser.parseHongCreditLine(acc)(line)
          }
          Source.fromFile(transactions.ref.file).getLines().toSet map parse
        }

        Transaction.append(txToAppend, txAlready) { txs =>
          GoogleSheet.appendValues(
            ws,
            request.accessToken,
            Config.sheetFileId.get,
            range,
            values = txs.map(Row.fromTransaction).toSeq
          )
        }
      }

      val result: Future[Either[String, Unit]] = {

        val txAlready = GoogleSheet.getValues(
          ws,
          request.accessToken,
          Config.sheetFileId.get,
          range
        )

        txAlready flatMap {
          case Left(msg) => Future.successful(Left(msg))
          case Right(rows) => appended(rows.map(Transaction.fromRow).toSet)
        }
      }

      result map {
        case Left(msg) => InternalServerError(msg)
        case Right(_) => Redirect(routes.TxController.viewTransactions())
      }

    } getOrElse {
      Future.successful(Ok("File upload failed"))
    }
  }

  def viewUploadTransactions() = AuthorizedAction {
    Ok(views.html.transactionsUpload())
  }
}
