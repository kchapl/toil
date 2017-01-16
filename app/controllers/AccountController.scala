package controllers

import model.AccountHandler.{account, allAccounts}
import play.api.mvc.Controller
import services.GoogleSheet

class AccountController extends Controller with Security {

  def viewAccounts = AuthorizedAction { implicit request =>
    Ok(views.html.accounts(allAccounts(request.accessToken)(GoogleSheet.fetchAllRows).toSeq))
  }

  def viewAccount(name: String) = AuthorizedAction { implicit request =>
    account(name, request.accessToken)(GoogleSheet.fetchAllRows) map { a =>
      Ok(views.html.account(a))
    } getOrElse {
      BadRequest(s"No account $name")
    }
  }
}
