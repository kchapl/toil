package controllers

import controllers.Attributes.credential
import model.Amount.sum
import model._
import play.api.mvc.{AbstractController, ControllerComponents}
import services.ValueService

class DashboardController(
  components: ControllerComponents,
  authAction: AuthorisedAction,
  values: ValueService,
  accountSheet: Sheet,
  transactionSheet: Sheet
) extends AbstractController(components) {

  def view = Action {
    Ok(views.html.dashboard())
  }

  def view2 = authAction { implicit request =>
    val dateBalances = {
      val allTransactions = values.allRows(transactionSheet, request.attrs(credential)).map(Transaction.fromRow)
      val allAccounts = values.allRows(accountSheet, request.attrs(credential)).map(Account.fromRow) map { account =>
        AccountAndTransactions(
          account,
          allTransactions.filter(_.account == account.name).toSet
        )
      }
      allAccounts
        .flatMap(_.dateBalances)
        .groupBy(_.date)
        .mapValues { amounts =>
          sum(amounts.map(_.amount))
        }
        .map {
          case (date, amount) => DateAmount(date, amount)
        }
        .toSeq
    }
    Ok(views.html.dashboard2(dateBalances))
  }
}
