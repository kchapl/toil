package controllers

import model.TransactionHandler.allTransactions
import model.{Organizer, TransactionHandler}
import play.api.mvc.Controller
import services.GoogleSheet

import scala.io.Source

class TransactionController extends Controller with Security {

  def viewTransactions = AuthorizedAction { implicit request =>
    val txs = allTransactions(request.accessToken)(GoogleSheet.fetchAllRows).toSeq
    val organized = Organizer.organize(txs, request.queryString)
    Ok(views.html.transactions(organized))
  }

  //noinspection TypeAnnotation
  def uploadTransactions = AuthorizedAction(parse.multipartFormData) { implicit request =>
    request.body.file("transactions").map { filePart =>
      val accountName = request.body.dataParts("account").head
      val source = Source.fromFile(filePart.ref.file)
      TransactionHandler.uploadTransactions(
        request.accessToken,
        accountName,
        source
      )(GoogleSheet.fetchAllRows)(GoogleSheet.appendRows)
      Redirect(routes.TransactionController.viewTransactions())
    } getOrElse {
      Ok("File upload failed")
    }
  }

  def viewUploadTransactions() = AuthorizedAction {
    Ok(views.html.transactionsUpload())
  }
}
