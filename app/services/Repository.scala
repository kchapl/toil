package services

import com.google.inject.ImplementedBy
import models.Account

import scala.concurrent.Future

@ImplementedBy(classOf[GoogleSheetRepository])
trait Repository {

  def fetchAllAccounts(accessToken: String): Future[Seq[Account]]
}
