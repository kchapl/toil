package controllers

import controllers.Helper.toTransaction
import model.{Surpluses, Transaction}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{Sheet, ValueService}

class SurplusController(
  components: ControllerComponents,
  authAction: AuthorisedAction,
  values: ValueService,
  transactionSheet: Sheet
) extends AbstractController(components) {

  private def fetchAllTransactions[A](request: CredentialRequest[A]): Seq[Transaction] =
    values.allRows(transactionSheet, request.credential).map(toTransaction)

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
