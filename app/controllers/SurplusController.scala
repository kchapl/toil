package controllers

import javax.inject.Inject

import models._
import play.api.libs.ws.WSClient
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext

class SurplusController @Inject()(ws: WSClient)(implicit context: ExecutionContext)
  extends Controller with Security {

  def viewSurplus = AuthorizedAction.async { implicit request =>
    Transaction.fetchAll(ws, request.accessToken) map {
      case Left(msg) =>
        InternalServerError(s"$msg")
      case Right(transactions) =>
        val surplus = transactions.groupBy { t =>
          (t.date.getYear, t.date.getMonth)
        }.map {
          case ((year, month), txs) => Surplus.fromTransactions(year, month, txs)
        }.toSeq
        Ok(views.html.surplus(surplus))
    }
  }

  def viewSurplusFigures = AuthorizedAction.async { implicit request =>
    Transaction.fetchAll(ws, request.accessToken) map {
      case Left(msg) =>
        InternalServerError(s"$msg")
      case Right(transactions) =>
        Ok(
          views.html.surplusFigures(
            transactions.filter(_.isIncome),
            transactions.filter(_.isSpend),
            transactions.filter(_.isTransfer)
          )
        )
    }
  }
}
