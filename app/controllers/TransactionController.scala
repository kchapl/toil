package controllers

import controllers.Helper.{allAccounts, allTransactions, transactionSheet}
import model.Account.byName
import model.{Transaction, TransactionHandler}
import play.api.mvc.Controller
import services.GoogleSheet
import util.Organiser

import scala.io.Source

class TransactionController extends Controller {

  private def toRow(tx: Transaction) = Seq(
    tx.account,
    tx.date.toString,
    tx.payee,
    tx.reference getOrElse "",
    tx.mode getOrElse "",
    tx.amount.pounds.toString,
    tx.category
  )

  def viewTransactions = AuthorisedAction { implicit request =>
    implicit val userId = request.session(UserId.key)
    val organised = Organiser.organise(allTransactions, request.queryString)
    Ok(views.html.transactions(organised))
  }

  def uploadTransactions = AuthorisedAction(parse.multipartFormData) { implicit request =>
    request.body.file("transactions").map { filePart =>
      val accountName = request.body.dataParts("account").head
      implicit val userId = request.session(UserId.key)
      allAccounts find byName(accountName) map { account =>
        val parsed = TransactionHandler.parsed(account, Source.fromFile(filePart.ref.file))
        GoogleSheet.appendRows(
          transactionSheet,
          rows = (parsed -- allTransactions).map(toRow).toSeq
        )
        Redirect(routes.TransactionController.viewTransactions())
      } getOrElse {
        InternalServerError(s"No such account: $accountName")
      }
    } getOrElse {
      Ok("File upload failed")
    }
  }

  def viewUploadTransactions() = AuthorisedAction { request =>
    implicit val userId = request.session(UserId.key)
    Ok(views.html.transactionsUpload(allAccounts))
  }

  def dedup = AuthorisedAction { implicit request =>
    implicit val userId = request.session(UserId.key)
    val deduped = allTransactions.distinct
    GoogleSheet.replaceAllRows(transactionSheet, deduped.map(toRow)) match {
      case Left(msg) => InternalServerError(msg)
      case Right(_) =>
        Redirect(routes.TransactionController.viewTransactions())
    }
  }
}
