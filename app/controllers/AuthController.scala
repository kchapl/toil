package controllers

import java.net.URLDecoder
import javax.inject.Inject

import model.Config.redirectUri
import model.Flow
import play.api.libs.ws.WSClient
import play.api.mvc.Codec.utf_8
import play.api.mvc.{Action, Controller}

class AuthController @Inject()(ws: WSClient) extends Controller {

  def authCallback(code: String, state: String) = Action { implicit request =>
    def decode(s: String) = URLDecoder.decode(s, utf_8.charset)
    val result = if (request.session.get("state").contains(state)) {
      val tokenResponse = Flow.readWrite.newTokenRequest(code).setRedirectUri(redirectUri).execute()
      Flow.readWrite.createAndStoreCredential(tokenResponse, UserId(request))
      val path = decode(state).split("&path=").last
      Redirect(path)
    } else {
      Unauthorized
    }
    result.removingFromSession(state)
  }
}
