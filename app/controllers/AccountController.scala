package controllers

import controllers.Attributes.credential
import model.{Account, AccountAndTransactions, Sheet, Transaction}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.ValueService

class AccountController(
    components: ControllerComponents,
    authAction: AuthorisedAction,
    values: ValueService,
    accountSheet: Sheet,
    transactionSheet: Sheet
) extends AbstractController(components) {

  def viewAccounts = authAction { implicit request =>
    val transactions =
      values.allRows(transactionSheet, request.attrs(credential)).map(Transaction.fromRow)
    Ok(
      views.html.accounts(
        values.allRows(accountSheet, request.attrs(credential)).map(Account.fromRow) map {
          account =>
            AccountAndTransactions(
              account,
              transactions.filter(_.account == account.name).toSet
            )
        }
      )
    )
  }
}
