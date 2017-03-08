package controllers

import controllers.Helper.{allAccounts, allTransactions}
import model.Account.byName
import model.{Account, AccountAndTransactions}
import play.api.mvc.Controller

class AccountController extends Controller {

  def viewAccounts = AuthorisedAction { implicit request =>
    implicit val userId = request.session(UserId.key)
    val accounts = allAccounts
    Ok(views.html.accounts(accounts))
  }

  def viewAccount(name: String) = AuthorisedAction { implicit request =>
    implicit val userId = request.session(UserId.key)
    allAccounts find byName(name) map { a =>
      val ts = allTransactions filter (_.account.toLowerCase == name.toLowerCase)
      Ok(views.html.account(AccountAndTransactions(a, ts.toSet)))
    } getOrElse {
      BadRequest(s"No account $name")
    }
  }
}
