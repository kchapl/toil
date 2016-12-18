package model

import javax.inject.Inject

import services.Repository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AccountHandler @Inject()(repo: Repository) {

  def allAccounts(accessToken: String): Future[Set[Account]] =
    repo.fetchAllAccounts(accessToken)

  def account(name: String, accessToken: String): Future[Option[Account]] =
    repo.fetchAllAccounts(accessToken) map (_ find (_.name.toLowerCase == name.toLowerCase))
}
