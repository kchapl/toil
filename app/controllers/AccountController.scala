package controllers

import javax.inject.Inject

import models.Account
import play.api.libs.ws.WSClient
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext.Implicits.global

class AccountController @Inject()(ws: WSClient) extends Controller with Security {

  def viewAccounts = AuthorizedAction.async { implicit request =>
    Account.fetchAll(ws, request.accessToken) map { as =>
      Ok(views.html.accounts(as))
    }
  }
}
