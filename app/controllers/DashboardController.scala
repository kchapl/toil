package controllers

import play.api.mvc.{Action, Controller}

class DashboardController extends Controller {

  def view = Action {
    Ok(views.html.dashboard())
  }
}
