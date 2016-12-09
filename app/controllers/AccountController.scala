package controllers

import javax.inject.Inject

import models.AccountHandler
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext.Implicits.global

class AccountController @Inject()(accHandler: AccountHandler) extends Controller with Security {

  def viewAccounts = AuthorizedAction.async { implicit request =>
    accHandler.allAccounts(request.accessToken) map { as =>
      Ok(views.html.accounts(as.toSeq))
    }
  }

  def viewAccount(name: String) = AuthorizedAction.async { implicit request =>
    accHandler.account(name, request.accessToken) map {
      _ map { a =>
        Ok(views.html.account(a))
      } getOrElse {
        BadRequest(s"No account $name")
      }
    }
  }
}
