package controllers

import java.math.BigInteger
import java.net.URLEncoder
import java.security.SecureRandom

import controllers.UserId.key
import play.api.mvc.Codec.utf_8
import play.api.mvc.Results.Redirect
import play.api.mvc._
import util.Flow

import scala.concurrent.{ExecutionContext, Future}

class AuthorisedAction(
  val parser: BodyParser[AnyContent],
  flow: Flow,
  redirectUri: String
)(
  implicit val executionContext: ExecutionContext
) extends ActionBuilder[Request, AnyContent]
  with ActionRefiner[Request, Request] {

  override protected def refine[A](request: Request[A]): Future[Either[Result, Request[A]]] =
    Future.successful {
      request.session.get(key) flatMap { userId =>
        Option(flow.readWrite.loadCredential(userId)) filter (_.getExpiresInSeconds > 0) map { credential =>
          Right(request.addAttr(Attributes.credential, credential))
        }
      } getOrElse Left(onUnauthorised(request, UserId(request)))
    }

  private def onUnauthorised[A](request: Request[A], userId: String): Result = {
    def encode(s: String) = URLEncoder.encode(s, utf_8.charset)
    val securityToken     = new BigInteger(130, new SecureRandom()).toString(32)
    val state             = encode(s"securityToken=$securityToken&path=${request.path}")
    val uri = flow.readWrite
      .newAuthorizationUrl()
      .setState(state)
      .setRedirectUri(redirectUri)
      .build()
    Redirect(uri).addingToSession("state" -> state, UserId.key -> userId)(request)
  }
}
