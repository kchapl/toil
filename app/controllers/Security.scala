package controllers

import java.math.BigInteger
import java.net.URLEncoder
import java.security.SecureRandom

import models.Config.{clientId, redirectUri}
import play.api.mvc.Results.TemporaryRedirect
import play.api.mvc._

import scala.concurrent.Future

trait Security {

  class AuthorizedRequest[A](request: Request[A], val accessToken: String) extends WrappedRequest[A](request)

  def accessToken[A](request: Request[A]): Option[String] = request.session.get("accessToken")

  def onUnauthorized[A](request: Request[A]): Result = {
    val scope = URLEncoder.encode("https://www.googleapis.com/auth/spreadsheets.readonly", "UTF-8")
    val securityToken = new BigInteger(130, new SecureRandom()).toString(32)
    val state = URLEncoder.encode(s"securityToken=$securityToken&path=${request.path}", "UTF-8")
    val queryString = s"response_type=code&client_id=$clientId&redirect_uri=$redirectUri&scope=$scope&state=$state"
    val url = s"https://accounts.google.com/o/oauth2/v2/auth?$queryString"

    TemporaryRedirect(url).addingToSession("state" -> state)(request)
  }

  object AuthorizedAction extends ActionBuilder[AuthorizedRequest] with ActionRefiner[Request, AuthorizedRequest] {

    def refine[A](request: Request[A]): Future[Either[Result, AuthorizedRequest[A]]] = Future.successful {
      accessToken(request) map { accessToken =>
        Right(new AuthorizedRequest(request, accessToken))
      } getOrElse {
        Left(onUnauthorized(request))
      }
    }
  }
}
