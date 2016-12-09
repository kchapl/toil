package controllers

import javax.inject.Inject

import play.api.mvc.Controller
import services.Repository

import scala.concurrent.ExecutionContext.Implicits.global

class AccountController @Inject()(repo: Repository) extends Controller with Security {

  def viewAccounts = AuthorizedAction.async { implicit request =>
    repo.fetchAllAccounts(request.accessToken) map { as =>
      Ok(views.html.accounts(as))
    }
  }
}
