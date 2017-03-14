package controllers

import controllers.Helper.{allAccounts, allTransactions, transactionSheet}
import model.{Category, Transaction}
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
    tx.category.code
  )

  def viewTransactions() = AuthorisedAction { request =>
    implicit val userId = request.session(UserId.key)
    val organised = Organiser.organise(allTransactions, request.queryString)
    Ok(views.html.transactions(organised))
  }

  def viewImportTransactions() = AuthorisedAction { request =>
    implicit val userId = request.session(UserId.key)
    Ok(views.html.transactionsImport(allAccounts))
  }

  def importTransactions() = AuthorisedAction(parse.multipartFormData) { request =>
    request.body.file("transactions") map { filePart =>
      implicit val userId = request.session(UserId.key)
      Transaction.toImport(
        before = allTransactions.toSet,
        accounts = allAccounts.toSet,
        accountName = request.body.dataParts("account").head,
        source = Source.fromFile(filePart.ref.file)
      ) match {
        case Left(f) => InternalServerError(f.description)
        case Right(ts) =>
          GoogleSheet.appendRows(
            transactionSheet,
            rows = ts.map(toRow).toSeq
          )
          Redirect(routes.TransactionController.viewTransactions())
      }
    } getOrElse {
      InternalServerError("Transaction import failed")
    }
  }

  def editTransactions() = AuthorisedAction { request =>
    implicit val userId = request.session(UserId.key)

    val ts = allTransactions
    val submitted = request.body.asFormUrlEncoded map {
      _ flatMap {
        case (hashCode, categoryCodes) =>
          Transaction.fromHashcode(ts)(hashCode.toInt) map {
            _.copy(category = Category.fromCode(categoryCodes.head))
          }
      }
    } getOrElse Nil

    if (submitted.map(_.category) != ts.map(_.category)) {
      GoogleSheet.replaceAllRows(transactionSheet, submitted.toSeq.map(toRow)) match {
        case Left(msg) => InternalServerError(msg)
        case Right(_) =>
          Redirect(routes.TransactionController.viewTransactions())
      }
    } else Redirect(routes.TransactionController.viewTransactions())
  }

  def dedupTransactions() = AuthorisedAction { request =>
    implicit val userId = request.session(UserId.key)
    val deduped = allTransactions.distinct
    GoogleSheet.replaceAllRows(transactionSheet, deduped.map(toRow)) match {
      case Left(msg) => InternalServerError(msg)
      case Right(_) =>
        Redirect(routes.TransactionController.viewTransactions())
    }
  }
}
