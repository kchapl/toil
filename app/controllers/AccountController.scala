package controllers

import controllers.Helper.{toAccount, toTransaction, transactionSheet}
import model.AccountAndTransactions
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{Sheet, ValueService}

class AccountController(components: ControllerComponents, authAction: AuthorisedAction, values: ValueService)
  extends AbstractController(components) {

  private val accountSheet = Sheet("Accounts", numCols = 3)

  def viewAccounts = authAction { implicit request =>
    val transactions = values.allRows(transactionSheet, request.credential).map(toTransaction)
    Ok(
      views.html.accounts(
        values.allRows(accountSheet, request.credential).map(toAccount) map { account =>
          AccountAndTransactions(
            account,
            transactions.filter(_.account == account.name).toSet
          )
        }
      )
    )
  }
}
