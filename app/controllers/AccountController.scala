package controllers

import controllers.Helper.{allAccounts, allTransactions}
import model.AccountAndTransactions
import play.api.mvc.{AbstractController, ControllerComponents}

class AccountController(components: ControllerComponents, authAction: AuthorisedAction)
  extends AbstractController(components) {

  def viewAccounts = authAction { implicit request =>
    val transactions = allTransactions(request.credential)
    Ok(
      views.html.accounts(
        allAccounts(request.credential) map { account =>
          AccountAndTransactions(
            account,
            transactions.filter(_.account == account.name).toSet
          )
        }
      )
    )
  }
}
