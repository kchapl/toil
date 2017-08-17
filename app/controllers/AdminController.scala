package controllers

import play.api.mvc.{AbstractController, ControllerComponents}
import services.ResourceService

class AdminController(components: ControllerComponents, authAction: AuthorisedAction, resourceService: ResourceService)
  extends AbstractController(components) {

  def viewResources() = authAction { implicit request =>
    Ok(views.html.resources(resourceService.fetchUsage(request.credential)))
  }
}
