package controllers

import play.api.mvc.{AbstractController, ControllerComponents}

class AdminController(components: ControllerComponents, authAction: AuthorisedAction)
  extends AbstractController(components) {

  def viewResources() = authAction { implicit request =>
    Ok("Will show resources")
  }
}
