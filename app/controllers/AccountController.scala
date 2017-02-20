package controllers

import model.AccountHandler.{account, allAccounts}
import play.api.mvc.Controller
import services.GoogleSheet

class AccountController extends Controller {

  def viewAccounts = AuthorisedAction { implicit request =>
    val userId = request.session(UserId.key)
    val accounts = allAccounts(GoogleSheet(userId).fetchAllRows).toSeq
    Ok(views.html.accounts(accounts))
  }

  def viewAccount(name: String) = AuthorisedAction { implicit request =>
    val userId = request.session(UserId.key)
    account(name)(GoogleSheet(userId).fetchAllRows) map { a =>
      Ok(views.html.account(a))
    } getOrElse {
      BadRequest(s"No account $name")
    }
  }
}
