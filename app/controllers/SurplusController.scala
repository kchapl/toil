package controllers

import controllers.Helper.allTransactions
import model.Surplus
import play.api.mvc.Controller

class SurplusController extends Controller {

  def viewSurplus = AuthorisedAction { implicit request =>
    implicit val userId = request.session(UserId.key)
    Surplus.fromTransactions(allTransactions.toSet) match {
      case Left(msg) => InternalServerError(msg)
      case Right(s)  => Ok(views.html.surplus(s))
    }
  }

  def viewSurplusFigures = AuthorisedAction { implicit request =>
    implicit val userId = request.session(UserId.key)
    val txs             = allTransactions
    Ok(
      views.html.surplusFigures(
        txs.filter(_.isIncome),
        txs.filter(_.isSpend),
        txs.filter(_.isTransfer),
        txs.filter(_.isRepayment),
        txs.filter(_.isRefund)
      )
    )
  }
}
