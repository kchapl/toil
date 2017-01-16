package controllers

import model.Surplus
import model.TransactionHandler.allTransactions
import play.api.mvc.Controller
import services.GoogleSheet

class SurplusController extends Controller with Security {

  def viewSurplus = AuthorizedAction { implicit request =>
    val surpluses =
      Surplus.fromTransactions(allTransactions(request.accessToken)(GoogleSheet.fetchAllRows))
    Ok(views.html.surplus(surpluses))
  }

  def viewSurplusFigures = AuthorizedAction { implicit request =>
    val txs = allTransactions(request.accessToken)(GoogleSheet.fetchAllRows)
    Ok(
      views.html.surplusFigures(
        txs.toSeq.filter(_.isIncome),
        txs.toSeq.filter(_.isSpend),
        txs.toSeq.filter(_.isTransfer)
      )
    )
  }
}
