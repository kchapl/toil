package controllers

import controllers.Helper.{allAccounts, allTransactions}
import model.AccountAndTransactions
import play.api.mvc.Controller

class AccountController extends Controller {

  def viewAccounts = AuthorisedAction { implicit request =>
    implicit val userId = request.session(UserId.key)
    val transactions    = allTransactions
    Ok(
      views.html.accounts(
        allAccounts map { account =>
          AccountAndTransactions(
            account,
            transactions.filter(_.account == account.name).toSet
          )
        }
      ))
  }
}
