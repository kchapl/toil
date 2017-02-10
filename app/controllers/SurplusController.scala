package controllers

import model.Surplus
import model.TransactionHandler.allTransactions
import play.api.mvc.Controller
import services.GoogleSheet

class SurplusController extends Controller {

  def viewSurplus = AuthorisedAction { implicit request =>
    val surpluses =
      Surplus.fromTransactions(
        allTransactions(request.session(UserId.key))
        (GoogleSheet.fetchAllRows)
      )
    Ok(views.html.surplus(surpluses))
  }

  def viewSurplusFigures = AuthorisedAction { implicit request =>
    val txs = allTransactions(request.session(UserId.key))(GoogleSheet.fetchAllRows)
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
