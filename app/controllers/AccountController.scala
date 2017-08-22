package controllers

import model.{Account, AccountAndTransactions, Transaction}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.{Sheet, ValueService}

class AccountController(
  components: ControllerComponents,
  authAction: AuthorisedAction,
  values: ValueService,
  accountSheet: Sheet,
  transactionSheet: Sheet
) extends AbstractController(components) {

  def viewAccounts = authAction { implicit request =>
    val transactions = values.allRows(transactionSheet, request.credential).map(Transaction.fromRow)
    Ok(
      views.html.accounts(
        values.allRows(accountSheet, request.credential).map(Account.fromRow) map { account =>
          AccountAndTransactions(
            account,
            transactions.filter(_.account == account.name).toSet
          )
        }
      )
    )
  }
}
