package controllers

import model.Surplus
import model.TransactionHandler.allTransactions
import play.api.mvc.Controller
import services.GoogleSheet

class SurplusController extends Controller {

  def viewSurplus = AuthorisedAction { implicit request =>
    val userId = request.session(UserId.key)
    Surplus.fromTransactions(allTransactions(GoogleSheet(userId).fetchAllRows)) match {
      case Left(msg) => InternalServerError(msg)
      case Right(s) => Ok(views.html.surplus(s))
    }
  }

  def viewSurplusFigures = AuthorisedAction { implicit request =>
    val userId = request.session(UserId.key)
    val txs = allTransactions(GoogleSheet(userId).fetchAllRows)
    Ok(
      views.html.surplusFigures(
        txs.toSeq.filter(_.isIncome),
        txs.toSeq.filter(_.isSpend),
        txs.toSeq.filter(_.isTransfer),
        txs.toSeq.filter(_.isRepayment),
        txs.toSeq.filter(_.isRefund)
      )
    )
  }
}
