package controllers

import javax.inject.Inject

import models.Config.txPath
import models.{Config, TxParser}
import play.api.libs.ws.WSClient
import play.api.mvc.Controller
import services.{GoogleSheet, TxImporter}

import scala.concurrent.ExecutionContext

class TxController @Inject()(ws: WSClient)(implicit context: ExecutionContext) extends Controller with Security {

  def viewTransactions() = AuthorizedAction { implicit request =>
    Ok(views.html.transactions(Nil))
  }

  def uploadTransactions() = AuthorizedAction { implicit request =>
    txPath map { path =>
      val lines = TxImporter.importTransactions(path)
      val transactions = lines map TxParser.parseLine
      Ok(views.html.transactions(transactions))
    } getOrElse {
      InternalServerError("Missing property 'TX_PATH'")
    }
  }
}
