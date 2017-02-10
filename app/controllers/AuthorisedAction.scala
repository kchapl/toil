package controllers

import java.math.BigInteger
import java.net.URLEncoder
import java.security.SecureRandom

import model.Config.redirectUri
import model.Flow
import play.api.mvc.Codec.utf_8
import play.api.mvc.Results.Redirect
import play.api.mvc._

import scala.concurrent.Future

object AuthorisedAction extends ActionBuilder[Request] with ActionFilter[Request] {

  private def onUnauthorised[A](request: Request[A], userId: String) = {
    def encode(s: String) = URLEncoder.encode(s, utf_8.charset)
    val securityToken = new BigInteger(130, new SecureRandom()).toString(32)
    val state = encode(s"securityToken=$securityToken&path=${ request.path }")
    val uri = Flow.readWrite.newAuthorizationUrl()
      .setState(state)
      .setRedirectUri(redirectUri)
      .build()
    Some(Redirect(uri).addingToSession("state" -> state, UserId.key -> userId)(request))
  }

  override protected def filter[A](request: Request[A]): Future[Option[Result]] =
    Future.successful {
      val userId = UserId(request)
      Option(Flow.readWrite.loadCredential(userId)) map { _ => None
      } getOrElse onUnauthorised(request, userId)
    }
}
