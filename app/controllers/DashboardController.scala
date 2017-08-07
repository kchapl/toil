package controllers

import play.api.mvc.{AbstractController, ControllerComponents}

class DashboardController(components: ControllerComponents) extends AbstractController(components) {

  def view = Action {
    Ok(views.html.dashboard())
  }
}
