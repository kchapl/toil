package controllers

import java.net.URLDecoder
import javax.inject.Inject

import model.Config.{clientId, clientSecret, redirectUri}
import play.api.libs.ws.WSClient
import play.api.mvc.Codec.utf_8
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthController @Inject()(ws: WSClient) extends Controller {

  def authCallback(code: String, state: String) = Action.async { implicit request =>

    if (!request.session.get("state").exists(URLDecoder.decode(_, utf_8.charset) == state)) {
      Future.successful(Unauthorized)
    } else {

      ws.url("https://www.googleapis.com/oauth2/v4/token").post(
        Map(
          "code" -> Seq(code),
          "client_id" -> Seq(clientId),
          "client_secret" -> Seq(clientSecret),
          "redirect_uri" -> Seq(redirectUri),
          "grant_type" -> Seq("authorization_code")
        )
      ) map { response =>
        val accessToken = (response.json \ "access_token").as[String]
        val startingPath = URLDecoder.decode(state, utf_8.charset).split("&path=").last
        Redirect(startingPath).removingFromSession("state").addingToSession("accessToken" -> accessToken)
      }
    }
  }
}
