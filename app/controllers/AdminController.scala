package controllers

import controllers.Attributes.credential
import play.api.mvc.{AbstractController, ControllerComponents}
import services.ResourceService

class AdminController(components: ControllerComponents, authAction: AuthorisedAction, resourceService: ResourceService)
  extends AbstractController(components) {

  def viewResources() = authAction { implicit request =>
    println(request.secure)
    println(request.headers)
    println(request.host)

    Ok(views.html.resources(resourceService.fetchUsage(request.attrs(credential))))
  }
}
