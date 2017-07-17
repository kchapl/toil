package controllers

import javax.inject.Inject

import play.api.mvc.{AbstractController, ControllerComponents}

class DashboardController @Inject()(components: ControllerComponents) extends AbstractController(components) {

  def view = Action {
    Ok(views.html.dashboard())
  }
}
