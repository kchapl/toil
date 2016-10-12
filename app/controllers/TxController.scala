package controllers

import play.api.mvc.{Action, Controller}
import services.TxImporter

import scala.util.Properties

class TxController extends Controller {

  private val txPath = Properties.envOrNone("TX_PATH")

  def viewTransactions() = Action {
    txPath map { p =>
      TxImporter.importTransactions(p)
      Ok
    } getOrElse {
      InternalServerError("Missing property 'TX_PATH'")
    }
  }
}
