package controllers

import java.net.URLDecoder

import play.api.libs.ws.WSClient
import play.api.mvc.Codec.utf_8
import play.api.mvc.{AbstractController, ControllerComponents}
import util.Flow

class AuthController(
    components: ControllerComponents,
    ws: WSClient,
    flow: Flow,
    redirectUri: String
) extends AbstractController(components) {

  def authCallback(code: String, state: String) = Action { implicit request =>
    def decode(s: String) = URLDecoder.decode(s, utf_8.charset)
    val result = if (request.session.get("state").contains(state)) {
      val tokenResponse = flow.readWrite.newTokenRequest(code).setRedirectUri(redirectUri).execute()
      flow.readWrite.createAndStoreCredential(tokenResponse, UserId(request))
      val path = decode(state).split("&path=").last
      Redirect(path)
    } else {
      Unauthorized
    }
    result.removingFromSession(state)
  }
}
