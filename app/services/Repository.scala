package services

import com.google.inject.ImplementedBy
import model.{Account, Transaction}

import scala.concurrent.Future

@ImplementedBy(classOf[GoogleSheetRepository])
trait Repository {

  def fetchAllTransactions(accessToken: String): Future[Set[Transaction]]

  def insertTransactions(accessToken: String, transactions: Set[Transaction]): Unit

  def fetchAllAccounts(accessToken: String): Future[Set[Account]]
}
