package controllers

import java.net.URLEncoder
import javax.inject.Inject

import models.Config
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller, Results}

import scala.concurrent.ExecutionContext.Implicits.global

class AuthController @Inject()(ws: WSClient) extends Controller {

  val clientId = URLEncoder.encode(Config.plainClientId.get, "UTF-8")
  val clientSecret = URLEncoder.encode(Config.plainClientSecret.get, "UTF-8")
  val redirectUri = Config.plainRedirectUri.get

  def authCallback(code: String) = Action.async { implicit request =>

    println("here")
    println(code)

    ws.url("https://www.googleapis.com/oauth2/v4/token").post(
      Map(
        "code" -> Seq(code),
        "client_id" -> Seq(clientId),
        "client_secret" -> Seq(clientSecret),
        "redirect_uri" -> Seq(redirectUri),
        "grant_type" -> Seq("authorization_code")
      )
    ) map { response =>

      println(response.status)
      println(response.json)
      println(response.json \ "access_token")
      val a = (response.json \ "access_token").as[String]

      // todo check documentation
      // todo check state
      // todo store access token in session
      // todo redirect to origin
      Results.SeeOther("/transactions").addingToSession("accessToken" -> a)
    }
  }
}
