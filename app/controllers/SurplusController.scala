package controllers

import controllers.Helper.allTransactions
import model.{Surpluses, Transaction}
import play.api.mvc.{AbstractController, ControllerComponents}

class SurplusController(components: ControllerComponents, authAction: AuthorisedAction)
  extends AbstractController(components) {

  def viewSurplus = authAction { implicit request =>
    val transactions = allTransactions(request.credential)
    Transaction.findAnomalies(transactions) map { anomalies =>
      Ok(views.html.anomalies(anomalies))
    } getOrElse {
      val surpluses = Surpluses.fromTransactions(transactions)
      Ok(views.html.surplus(surpluses))
    }
  }
}
