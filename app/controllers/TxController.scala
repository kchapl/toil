package controllers

import java.math.BigInteger
import java.net.URLEncoder
import java.security.SecureRandom
import javax.inject.Inject

import models.Config
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext

class TxController @Inject()(ws: WSClient)(implicit context: ExecutionContext) extends Controller with Security {

  def viewTransactions() = AuthorizedAction { implicit request =>

    println("*1")
    println(request.accessToken)

    val scope = URLEncoder.encode("https://www.googleapis.com/auth/spreadsheets.readonly", "UTF-8")
    val state = URLEncoder.encode(new BigInteger(130, new SecureRandom()).toString(32), "UTF-8")
    //request.session().attribute("state", state);

    val qs = s"response_type=code&client_id=$clientId&redirect_uri=$redirectUri&scope=$scope&state=$state"

    val url = s"https://accounts.google.com/o/oauth2/v2/auth?$qs"

    TemporaryRedirect(url)

    //    txPath map { path =>
    //      val transactions = TxImporter.importTransactions(path)
    //      Ok(views.html.transactions(transactions))
    //    } getOrElse {
    //      InternalServerError("Missing property 'TX_PATH'")
    //    }

  }
}
