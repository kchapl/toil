package config

import java.io.File
import java.net.URLEncoder

import controllers._
import play.api.ApplicationLoader.Context
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.api.{Application, ApplicationLoader, BuiltInComponentsFromContext, LoggerConfigurator}
import play.filters.HttpFiltersComponents
import router.Routes
import services.{GoogleSheetService, Sheet}
import util.Flow

import scala.util.Properties

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
    super.httpFilters.filterNot(_ == securityHeadersFilter)

  lazy val router: Router = {

    def urlEncode(s: String) = URLEncoder.encode(s, "UTF-8")

    val flow = new Flow(
      clientId = urlEncode(configuration.get[String]("app.client.id")),
      clientSecret = urlEncode(configuration.get[String]("app.client.secret")),
      fileStore = new File(Properties.tmpDir)
    )

    val redirectUri = configuration.get[String]("app.redirect.uri")

    val authAction = new AuthorisedAction(defaultBodyParser, flow, redirectUri)

    val googleSheets = new GoogleSheetService(
      appName = configuration.get[String]("app.name"),
      sheetFileId = configuration.get[String]("app.sheet.file.id")
    )

    val accountSheet     = Sheet("Accounts", numCols = 3)
    val transactionSheet = Sheet("Transactions", numCols = 7)

    new Routes(
      httpErrorHandler,
      assets,
      new DashboardController(controllerComponents),
      new AuthController(controllerComponents, wsClient, flow, redirectUri),
      new AccountController(controllerComponents, authAction, googleSheets, accountSheet, transactionSheet),
      new TransactionController(controllerComponents, authAction, googleSheets, accountSheet, transactionSheet),
      new SurplusController(controllerComponents, authAction, googleSheets, transactionSheet),
      new AdminController(controllerComponents, authAction, googleSheets)
    )
  }
}
