package controllers

import model.AccountHandler.{account, allAccounts}
import play.api.mvc.Controller
import services.GoogleSheet

class AccountController extends Controller {

  def viewAccounts = AuthorisedAction { implicit request =>
    val accounts = allAccounts(request.session(UserId.key))(GoogleSheet.fetchAllRows).toSeq
    Ok(views.html.accounts(accounts))
  }

  def viewAccount(name: String) = AuthorisedAction { implicit request =>
    account(name, request.session(UserId.key))(GoogleSheet.fetchAllRows) map { a =>
      Ok(views.html.account(a))
    } getOrElse {
      BadRequest(s"No account $name")
    }
  }
}
