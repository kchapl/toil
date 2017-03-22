package controllers

import controllers.Helper.allTransactions
import model.Surpluses
import play.api.mvc.Controller

class SurplusController extends Controller {

  def viewSurplus = AuthorisedAction { implicit request =>
    implicit val userId = request.session(UserId.key)
    val s = Surpluses.fromTransactions(allTransactions)
    if (s.hasUncategorised) {
      InternalServerError("Not entirely categorised")
    } else Ok(views.html.surplus(s))
  }
}
