package controllers

import cats.Monoid.combineAll
import controllers.Attributes.credential
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

  def view = authAction { implicit request =>
    val allTransactions =
      values.allRows(transactionSheet, request.attrs(credential)).map(Transaction.fromRow)
    val allAccounts = values
      .allRows(accountSheet, request.attrs(credential))
      .map(Account.fromRow)
      .map { account =>
        AccountAndTransactions(
          account,
          allTransactions.filter(_.account == account.name).toSet
        )
      }
      .sortBy(_.latestTransaction.date.toString)
    val dateBalances = {
      allAccounts
        .flatMap(_.dateBalances)
        .groupBy(_.date)
        .mapValues { amounts =>
          combineAll(amounts.map(_.amount))
        }
        .map {
          case (date, amount) => DateAmount(date, amount)
        }
    }.toSeq.sortBy(_.date.toString)
    Ok(views.html.dashboard(dateBalances, allAccounts))
  }
}
