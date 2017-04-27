package controllers

import controllers.Helper.allTransactions
import model.{Surpluses, Transaction}
import play.api.mvc.Controller

class SurplusController extends Controller {

  def viewSurplus = AuthorisedAction { implicit request =>
    implicit val userId = request.session(UserId.key)
    val transactions = allTransactions
    Transaction.findAnomalies(transactions) map { anomalies =>
      Ok(views.html.anomalies(anomalies))
    } getOrElse {
      val surpluses = Surpluses.fromTransactions(transactions)
      Ok(views.html.surplus(surpluses))
    }
  }
}
