package controllers

import model.TransactionHandler.allTransactions
import model.{Organiser, Transaction, TransactionHandler}
import play.api.mvc.Controller
import services.GoogleSheet

import scala.io.Source

class TransactionController extends Controller {

  def viewTransactions = AuthorisedAction { implicit request =>
    val userId = request.session(UserId.key)
    val txs = allTransactions(GoogleSheet(userId).fetchAllRows).toSeq
    val organised = Organiser.organise(txs, request.queryString)
    Ok(views.html.transactions(organised))
  }

  //noinspection TypeAnnotation
  def uploadTransactions = AuthorisedAction(parse.multipartFormData) { implicit request =>
    request.body.file("transactions").map { filePart =>
      val accountName = request.body.dataParts("account").head
      val source = Source.fromFile(filePart.ref.file)
      val userId = request.session(UserId.key)
      val sheet = GoogleSheet(userId)
      TransactionHandler.uploadTransactions(
        accountName,
        source
      )(sheet.fetchAllRows)(sheet.appendRows)
      Redirect(routes.TransactionController.viewTransactions())
    } getOrElse {
      Ok("File upload failed")
    }
  }

  def viewUploadTransactions() = AuthorisedAction {
    Ok(views.html.transactionsUpload())
  }

  def dedup = AuthorisedAction { implicit request =>
    Transaction.dedup(request.session(UserId.key)) match {
      case Left(msg) => InternalServerError(msg)
      case _ => Redirect(routes.TransactionController.viewTransactions())
    }
  }
}
