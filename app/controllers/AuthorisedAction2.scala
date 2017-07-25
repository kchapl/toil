package controllers

import java.math.BigInteger
import java.net.URLEncoder
import java.security.SecureRandom
import javax.inject.Inject

import play.api.mvc.Codec.utf_8
import play.api.mvc.Results.Redirect
import play.api.mvc._
import util.Config.redirectUri
import util.Flow

import scala.concurrent.{ExecutionContext, Future}

class AuthorisedAction2 @Inject()(val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[CredentialRequest, AnyContent]
  with ActionRefiner[Request, CredentialRequest] {

  override protected def refine[A](request: Request[A]): Future[Either[Result, CredentialRequest[A]]] =
    Future.successful {
      val userId = UserId(request)
      Option(Flow.readWrite.loadCredential(userId)) filter (_.getExpiresInSeconds > 0) map { credential =>
        Right(new CredentialRequest(credential, request))
      } getOrElse Left(onUnauthorised(request, userId))
    }

  private def onUnauthorised[A](request: Request[A], userId: String): Result = {
    def encode(s: String) = URLEncoder.encode(s, utf_8.charset)
    val securityToken     = new BigInteger(130, new SecureRandom()).toString(32)
    val state             = encode(s"securityToken=$securityToken&path=${request.path}")
    val uri = Flow.readWrite
      .newAuthorizationUrl()
      .setState(state)
      .setRedirectUri(redirectUri)
      .build()
    Redirect(uri).addingToSession("state" -> state, UserId.key -> userId)(request)
  }
}
