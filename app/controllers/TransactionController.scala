package controllers

import java.io.File

import play.api.mvc.{Action, Controller}
import services.TransactionImporter

import scala.util.Properties

class TransactionController extends Controller {

  private val txFileProp = Properties.envOrNone("TX_FILE") map (new File(_))

  def viewTransactions() = Action {

    val model = for {
      txFile <- txFileProp
    } yield {
      TransactionImporter.importTransactions(txFile)
    }

    model map (m => Ok) getOrElse InternalServerError("Missing property 'TX_FILE'")
  }
}
