package controllers

import javax.inject.Inject

import model.{Surplus, TransactionHandler}
import models._
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext

class SurplusController @Inject()(txHandler: TransactionHandler)(implicit context: ExecutionContext)
  extends Controller with Security {

  def viewSurplus = AuthorizedAction.async { implicit request =>
    txHandler.allTransactions(request.accessToken) map { txs =>
      val surpluses = Surplus.fromTransactions(txs)
      Ok(views.html.surplus(surpluses))
    }
  }

  def viewSurplusFigures = AuthorizedAction.async { implicit request =>
    txHandler.allTransactions(request.accessToken) map { txs =>
      Ok(
        views.html.surplusFigures(
          txs.toSeq.filter(_.isIncome),
          txs.toSeq.filter(_.isSpend),
          txs.toSeq.filter(_.isTransfer)
        )
      )
    }
  }
}
