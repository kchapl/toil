package controllers

import javax.inject.Inject

import controllers.Helper.{allAccounts, allTransactions}
import model.AccountAndTransactions
import play.api.mvc.{AbstractController, ControllerComponents}

class AccountController @Inject()(components: ControllerComponents, authorisedAction: AuthorisedAction)
  extends AbstractController(components) {

  def viewAccounts = authorisedAction { implicit request =>
    implicit val userId = request.session(UserId.key)
    val transactions    = allTransactions(request.credential)
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
