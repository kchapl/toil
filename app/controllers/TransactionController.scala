package controllers

import controllers.Attributes.credential
import model._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.libs.Files
import play.api.mvc._
import services.ValueService

import scala.io.Source
import scala.util.{Failure, Success}

class TransactionController(
    components: ControllerComponents,
    authAction: AuthorisedAction,
    values: ValueService,
    accountSheet: Sheet,
    transactionSheet: Sheet
) extends AbstractController(components)
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

  private def fetch[A, B](sheet: Sheet, request: Request[B], f: Row => A): Seq[A] =
    values.allRows(sheet, request.attrs(credential)).map(f)

  private def fetchAllAccounts[A](request: Request[A]): Seq[Account] =
    fetch(accountSheet, request, Account.fromRow)

  private def fetchAllTransactions[A](request: Request[A]): Seq[Transaction] =
    fetch(transactionSheet, request, Transaction.fromRow)

  def viewTransactions() = authAction { implicit request =>
    Ok(views.html.transactions(fetchAllTransactions(request)))
  }

  def viewImportTransactions() = authAction { implicit request =>
    Ok(views.html.transactionsImport(fetchAllAccounts(request)))
  }

  def importTransactions(): Action[MultipartFormData[Files.TemporaryFile]] =
    authAction(parse.multipartFormData) { request =>
      request.body.file("transactions") map { filePart =>
        Transaction.toImport(
          before = fetchAllTransactions(request).toSet,
          accounts = fetchAllAccounts(request).toSet,
          accountName = request.body.dataParts("account").head,
          source = Source.fromFile(filePart.ref.path.toFile)
        ) match {
          case Left(f) => InternalServerError(f.description)
          case Right(ts) =>
            values.appendRows(
              transactionSheet,
              rows = ts.map(toRow).toSeq,
              request.attrs(credential)
            )
            Redirect(routes.TransactionController.viewTransactions())
        }
      } getOrElse {
        InternalServerError("Transaction import failed")
      }
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
    Ok(views.html.transactionAdd(transactionForm, fetchAllAccounts(request)))
  }

  def addTransaction(): Action[TransactionBinding] = authAction(parse.form(transactionForm)) {
    implicit request =>
      val transaction = Transaction.fromBinding(request.body)
      values.appendRows(transactionSheet, Seq(toRow(transaction)), request.attrs(credential))
      Redirect(routes.TransactionController.viewTransactions())
  }

  def updateTransaction(): Action[AnyContent] = authAction { request =>
    val result = for {
      hashcode <- request.body.asFormUrlEncoded.get("id").headOption.map(_.toInt)
      category <- request.body.asFormUrlEncoded.get("cat").headOption
      rowIndex <- Transaction.indexFromHashcode(hashcode)(fetchAllTransactions(request))
    } yield {
      val update = values.updateCellValue(
        sheetName = transactionSheet.name,
        rowIdx = rowIndex,
        colIdx = Transaction.categoryColumnIndex,
        updateTo = category,
        credential = request.attrs(credential)
      )
      update match {
        case Failure(e) => InternalServerError(e.getMessage)
        case Success(_) => NoContent
      }
    }
    result getOrElse BadRequest("Missing or invalid ID field")
  }

  def dedupTransactions() = authAction { request =>
    val deduped = fetchAllTransactions(request).distinct
    values.replaceAllRows(
      transactionSheet,
      deduped.map(toRow),
      request.attrs(credential)
    ) match {
      case Left(msg) => InternalServerError(msg)
      case Right(_) =>
        Redirect(routes.TransactionController.viewTransactions())
    }
  }
}
