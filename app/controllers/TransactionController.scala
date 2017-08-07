package controllers

import controllers.Helper.{allAccounts, allTransactions, transactionSheet}
import model.{Category, Transaction, Uncategorised}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}
import services.GoogleSheet

import scala.io.Source

class TransactionController(components: ControllerComponents, authAction: AuthorisedAction)
  extends AbstractController(components)
  with I18nSupport {

  private def toRow(tx: Transaction) = Seq(
    tx.account,
    tx.date.toString,
    tx.payee,
    tx.reference getOrElse "",
    tx.mode getOrElse "",
    tx.amount.pounds.toString,
    tx.category.code
  )

  def viewTransactions() = authAction { implicit request =>
    Ok(views.html.transactions(allTransactions(request.credential)))
  }

  def viewImportTransactions() = authAction { implicit request =>
    Ok(views.html.transactionsImport(allAccounts(request.credential)))
  }

  def importTransactions() = authAction(parse.multipartFormData) { request =>
    request.body.file("transactions") map { filePart =>
      Transaction.toImport(
        before = allTransactions(request.credential).toSet,
        accounts = allAccounts(request.credential).toSet,
        accountName = request.body.dataParts("account").head,
        source = Source.fromFile(filePart.ref.path.toFile)
      ) match {
        case Left(f) => InternalServerError(f.description)
        case Right(ts) =>
          GoogleSheet.appendRows(
            transactionSheet,
            rows = ts.map(toRow).toSeq,
            request.credential
          )
          Redirect(routes.TransactionController.viewTransactions())
      }
    } getOrElse {
      InternalServerError("Transaction import failed")
    }
  }

  def editTransactions() = authAction { request =>
    implicit val transactions = allTransactions(request.credential).toSet
    val submitted = (request.body.asFormUrlEncoded map {
      _ flatMap {
        case ("csrfToken", _)           => None
        case ("transactions_length", _) => None
        case (hashCode, categoryCodes) =>
          Transaction.fromHashcode(hashCode.toInt) map {
            _.copy(category = Category.fromCode(categoryCodes.head))
          }
      }
    } getOrElse Nil).toSet
    if (Transaction.haveChanged(submitted)) {
      GoogleSheet.replaceAllRows(
        transactionSheet,
        Transaction.replace(submitted).toSeq.map(toRow),
        request.credential
      ) match {
        case Left(msg) => InternalServerError(msg)
        case Right(_)  => Redirect(routes.TransactionController.viewTransactions())
      }
    } else Redirect(routes.TransactionController.viewTransactions())
  }

  val transactionForm = Form(
    mapping(
      "account"   -> text,
      "date"      -> date,
      "payee"     -> text,
      "reference" -> optional(text),
      "mode"      -> optional(text),
      "amount"    -> text,
      "category"  -> default(text, Uncategorised.code)
    )(TransactionBinding.apply)(TransactionBinding.unapply)
  )

  def viewAddTransaction = authAction { implicit request =>
    Ok(views.html.transactionAdd(transactionForm, allAccounts(request.credential)))
  }

  def addTransaction() = authAction(parse.form(transactionForm)) { implicit request =>
    val transaction = Transaction.fromBinding(request.body)
    GoogleSheet.appendRows(transactionSheet, Seq(toRow(transaction)), request.credential)
    Redirect(routes.TransactionController.viewTransactions())
  }

  def dedupTransactions() = authAction { request =>
    val deduped = allTransactions(request.credential).distinct
    GoogleSheet.replaceAllRows(
      transactionSheet,
      deduped.map(toRow),
      request.credential
    ) match {
      case Left(msg) => InternalServerError(msg)
      case Right(_) =>
        Redirect(routes.TransactionController.viewTransactions())
    }
  }
}
