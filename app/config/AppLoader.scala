package config

import controllers._
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router
import play.api.{ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import play.filters.HttpFiltersComponents
import router.Routes

class AppLoader extends ApplicationLoader {
  def load(ctx: Context) = {
    LoggerConfigurator(ctx.environment.classLoader) foreach {
      _.configure(ctx.environment, ctx.initialConfiguration, Map.empty)
    }
    new Components(ctx).application
  }
}

class Components(ctx: Context)
  extends BuiltInComponentsFromContext(ctx)
  with HttpFiltersComponents
  with AssetsComponents
  with AhcWSComponents {

  // todo reinstate
  override def httpFilters =
    super.httpFilters.filterNot(filter => filter == securityHeadersFilter || filter == allowedHostsFilter)

  lazy val router: Router = {
    val authAction = new AuthorisedAction(defaultBodyParser)
    new Routes(
      httpErrorHandler,
      assets,
      new DashboardController(controllerComponents),
      new AuthController(controllerComponents, wsClient),
      new AccountController(controllerComponents, authAction),
      new TransactionController(controllerComponents, authAction),
      new SurplusController(controllerComponents, authAction),
      new AdminController(controllerComponents, authAction)
    )
  }
}
