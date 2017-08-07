package controllers

import javax.inject.Inject

import controllers.Helper.{allAccounts, allTransactions, transactionSheet}
import model.{Category, Transaction, Uncategorised}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}
import services.GoogleSheet

import scala.io.Source

class TransactionController @Inject()(components: ControllerComponents, authorisedAction: AuthorisedAction)
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

  def viewTransactions() = authorisedAction { implicit request =>
    Ok(views.html.transactions(allTransactions(request.credential)))
  }

  def viewImportTransactions() = authorisedAction { implicit request =>
    Ok(views.html.transactionsImport(allAccounts(request.credential)))
  }

  def importTransactions() = authorisedAction(parse.multipartFormData) { request =>
    request.body.file("transactions") map { filePart =>
      implicit val userId = request.session(UserId.key)
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
            rows = ts.map(toRow).toSeq
          )
          Redirect(routes.TransactionController.viewTransactions())
      }
    } getOrElse {
      InternalServerError("Transaction import failed")
    }
  }

  def editTransactions() = authorisedAction { request =>
    implicit val userId       = request.session(UserId.key)
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
      GoogleSheet.replaceAllRows(transactionSheet, Transaction.replace(submitted).toSeq.map(toRow)) match {
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

  def viewAddTransaction = authorisedAction { implicit request =>
    Ok(views.html.transactionAdd(transactionForm, allAccounts(request.credential)))
  }

  def addTransaction() = authorisedAction(parse.form(transactionForm)) { implicit request =>
    implicit val userId = request.session(UserId.key)
    val transaction     = Transaction.fromBinding(request.body)
    GoogleSheet.appendRows(transactionSheet, Seq(toRow(transaction)))
    Redirect(routes.TransactionController.viewTransactions())
  }

  def dedupTransactions() = authorisedAction { request =>
    implicit val userId = request.session(UserId.key)
    val deduped         = allTransactions(request.credential).distinct
    GoogleSheet.replaceAllRows(transactionSheet, deduped.map(toRow)) match {
      case Left(msg) => InternalServerError(msg)
      case Right(_) =>
        Redirect(routes.TransactionController.viewTransactions())
    }
  }
}
