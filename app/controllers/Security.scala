package controllers

import java.math.BigInteger
import java.net.URLEncoder
import java.security.SecureRandom

import model.Config.{clientId, redirectUri}
import play.api.mvc.Codec.utf_8
import play.api.mvc.Results.Redirect
import play.api.mvc._

import scala.concurrent.Future

trait Security {

  class AuthorizedRequest[A](request: Request[A], val accessToken: String)
    extends WrappedRequest[A](request)

  def accessToken[A](request: Request[A]): Option[String] =
    request.session.get("accessToken")

  def onUnauthorized[A](request: Request[A]): Result = {
    // todo: use this initially and then extend to write access
//    val scope = URLEncoder.encode(
//      "https://www.googleapis.com/auth/spreadsheets.readonly",
//      utf_8.charset
//    )
    val scope = URLEncoder.encode(
      "https://www.googleapis.com/auth/spreadsheets",
      utf_8.charset
    )
    val securityToken = new BigInteger(130, new SecureRandom()).toString(32)
    val state = URLEncoder.encode(
      s"securityToken=$securityToken&path=${request.path}",
      utf_8.charset
    )
    val queryString = "response_type=code&" +
      s"client_id=$clientId&" +
      s"redirect_uri=$redirectUri&" +
      s"scope=$scope&" +
      s"state=$state"
    val url = s"https://accounts.google.com/o/oauth2/v2/auth?$queryString"

    Redirect(url).addingToSession("state" -> state)(request)
  }

  object AuthorizedAction extends ActionBuilder[AuthorizedRequest]
    with ActionRefiner[Request, AuthorizedRequest] {

    def refine[A](request: Request[A]): Future[Either[Result,
      AuthorizedRequest[A]]] = Future.successful {
      accessToken(request) map { accessToken =>
        Right(new AuthorizedRequest(request, accessToken))
      } getOrElse {
        Left(onUnauthorized(request))
      }
    }
  }
}
