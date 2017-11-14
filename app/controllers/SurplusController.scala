package controllers

import controllers.Attributes.credential
import model.{Sheet, Surpluses, Transaction}
import play.api.mvc.{AbstractController, ControllerComponents, Request}
import services.ValueService

class SurplusController(
  components: ControllerComponents,
  authAction: AuthorisedAction,
  values: ValueService,
  transactionSheet: Sheet
) extends AbstractController(components) {

  private def fetchAllTransactions[A](request: Request[A]): Seq[Transaction] =
    values.allRows(transactionSheet, request.attrs(credential)).map(Transaction.fromRow)

  def viewSurplus = authAction { implicit request =>
    val transactions = fetchAllTransactions(request)
    Transaction.findAnomalies(transactions) map { anomalies =>
      Ok(views.html.anomalies(anomalies))
    } getOrElse {
      val surpluses = Surpluses.fromTransactions(transactions)
      Ok(views.html.surplus(surpluses))
    }
  }
}
