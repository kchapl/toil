package config

import controllers._
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import play.filters.HttpFiltersComponents
import router.Routes
import services.{GoogleSheet2, Sheet}

class AppLoader extends ApplicationLoader {
  override def load(ctx: Context): Application = {
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
  override def httpFilters: Seq[EssentialFilter] =
    super.httpFilters.filterNot(filter => filter == securityHeadersFilter || filter == allowedHostsFilter)

  lazy val router: Router = {
    val authAction = new AuthorisedAction(defaultBodyParser)

    val googleSheetService = new GoogleSheet2(
      appName = configuration.get[String]("app.name"),
      sheetFileId = configuration.get[String]("sheet.file.id")
    )

    val accountSheet     = Sheet("Accounts", numCols = 3)
    val transactionSheet = Sheet("Transactions", numCols = 7)

    new Routes(
      httpErrorHandler,
      assets,
      new DashboardController(controllerComponents),
      new AuthController(controllerComponents, wsClient),
      new AccountController(controllerComponents, authAction, googleSheetService),
      new TransactionController(controllerComponents, authAction, googleSheetService, accountSheet, transactionSheet),
      new SurplusController(controllerComponents, authAction, googleSheetService, transactionSheet),
      new AdminController(controllerComponents, authAction, googleSheetService)
    )
  }
}