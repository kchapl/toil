package controllers

import javax.inject.Inject

import model.{GapFiller, Organizer, TransactionHandler}
import models._
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext
import scala.io.Source

class TransactionController @Inject()(txHandler: TransactionHandler)
  (implicit context: ExecutionContext)
  extends Controller with Security {

  def viewTransactions = AuthorizedAction.async { implicit request =>


    GapFiller


    txHandler.allTransactions(request.accessToken) map { txs =>
      val organized = Organizer.organize(txs.toSeq, request.queryString)
      Ok(views.html.transactions(organized))
    }
  }

  def uploadTransactions = AuthorizedAction(parse.multipartFormData) { implicit request =>
    request.body.file("transactions").map { filePart =>
      val accountName = request.body.dataParts("account").head
      txHandler.uploadTransactions(request.accessToken, accountName, Source.fromFile(filePart.ref.file))
      Redirect(routes.TransactionController.viewTransactions())
    } getOrElse {
      Ok("File upload failed")
    }
  }

  def viewUploadTransactions() = AuthorizedAction {
    Ok(views.html.transactionsUpload())
  }
}
