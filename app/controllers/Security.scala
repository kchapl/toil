package controllers

import java.math.BigInteger
import java.net.URLEncoder
import java.security.SecureRandom

import models.Config
import play.api.mvc._

import scala.concurrent.Future

trait Security {

  val clientId = URLEncoder.encode(Config.plainClientId.get, "UTF-8")
  val redirectUri = Config.plainRedirectUri.get

  class AuthorizedRequest[A](request: Request[A], val accessToken: String) extends WrappedRequest[A](request)

  //  object AuthorizedAction extends AuthenticatedBuilder(req => accessToken(req))

  def accessToken[A](request: Request[A]): Option[String] = request.session.get("accessToken")

  def onUnauthorized[A](request: Request[A]): Result = {
    val scope = URLEncoder.encode("https://www.googleapis.com/auth/spreadsheets.readonly", "UTF-8")
    val state = URLEncoder.encode(new BigInteger(130, new SecureRandom()).toString(32), "UTF-8")
    //request.session().attribute("state", state);

    val qs = s"response_type=code&client_id=$clientId&redirect_uri=$redirectUri&scope=$scope&state=$state"

    val url = s"https://accounts.google.com/o/oauth2/v2/auth?$qs"

    Results.TemporaryRedirect(url)
  }

  object AuthorizedAction extends ActionBuilder[AuthorizedRequest] with ActionRefiner[Request, AuthorizedRequest] {

    def refine[A](request: Request[A]): Future[Either[Result, AuthorizedRequest[A]]] = {
      accessToken(request) map { accessToken =>
        Future.successful {
          Right(new AuthorizedRequest(request, accessToken))
        }
      } getOrElse {
        Future.successful(Left(onUnauthorized(request)))
      }
    }
  }
}
