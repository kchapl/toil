package controllers

import play.api.mvc.{Action, Controller}
import services.TxImporter

import scala.util.Properties

class TxController extends Controller {

  private val txPath = Properties.envOrNone("TX_PATH")

  def viewTransactions() = Action {
    txPath map { path =>
      val transactions = TxImporter.importTransactions(path)
      Ok(views.html.transactions(transactions))
    } getOrElse {
      InternalServerError("Missing property 'TX_PATH'")
    }
  }
}
