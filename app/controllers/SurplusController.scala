package controllers

import javax.inject.Inject

import controllers.Helper.allTransactions
import model.{Surpluses, Transaction}
import play.api.mvc.{AbstractController, ControllerComponents}

class SurplusController @Inject()(components: ControllerComponents, authorisedAction: AuthorisedAction)
  extends AbstractController(components) {

  def viewSurplus = authorisedAction { implicit request =>
    implicit val userId = request.session(UserId.key)
    val transactions    = allTransactions
    Transaction.findAnomalies(transactions) map { anomalies =>
      Ok(views.html.anomalies(anomalies))
    } getOrElse {
      val surpluses = Surpluses.fromTransactions(transactions)
      Ok(views.html.surplus(surpluses))
    }
  }
}
