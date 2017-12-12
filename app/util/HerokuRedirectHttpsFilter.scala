package util

import javax.inject.{Inject, Provider, Singleton}

import play.api.http.HeaderNames._
import play.api.http.Status._
import play.api.inject.{SimpleModule, bind}
import play.api.mvc._
import play.api.{Configuration, Environment, Mode}
import play.api.Logger

/**
  * Based on play.filters.https.RedirectHttpsFilter
  */
@Singleton
class HerokuRedirectHttpsFilter @Inject()(config: RedirectHttpsConfiguration)
    extends EssentialFilter {

  import util.RedirectHttpsKeys._
  import config._

  private val logger = Logger(getClass)

  private[this] lazy val stsHeaders = {
    if (!redirectEnabled) Seq.empty
    else strictTransportSecurity.toSeq.map(STRICT_TRANSPORT_SECURITY -> _)
  }

  override def apply(next: EssentialAction): EssentialAction = EssentialAction { req =>
    import play.api.libs.streams.Accumulator
    import scala.concurrent.ExecutionContext.Implicits.global

    def isSecure(req: RequestHeader) = req.headers.get("X-Forwarded-Proto").contains("https")

    if (isSecure(req)) {
      next(req).map(_.withHeaders(stsHeaders: _*))
    } else if (redirectEnabled) {
      Accumulator.done(Results.Redirect(createHttpsRedirectUrl(req), redirectStatusCode))
    } else {
      logger.info(s"Not redirecting to HTTPS because $redirectEnabledPath flag is not set.")
      next(req)
    }
  }

  protected def createHttpsRedirectUrl(req: RequestHeader): String = {
    import req.{domain, uri}
    sslPort match {
      case None | Some(443) =>
        s"https://$domain$uri"
      case Some(port) =>
        s"https://$domain:$port$uri"
    }
  }
}

case class RedirectHttpsConfiguration(
    strictTransportSecurity: Option[String] = Some("max-age=31536000; includeSubDomains"),
    redirectStatusCode: Int = PERMANENT_REDIRECT,
    sslPort: Option[Int] = None, // should match up to ServerConfig.sslPort
    redirectEnabled: Boolean = true
) {
  def hstsEnabled: Boolean = redirectEnabled && strictTransportSecurity.isDefined
}

private object RedirectHttpsKeys {
  val stsPath             = "play.filters.https.strictTransportSecurity"
  val statusCodePath      = "play.filters.https.redirectStatusCode"
  val portPath            = "play.filters.https.port"
  val redirectEnabledPath = "play.filters.https.redirectEnabled"
}

@Singleton
class RedirectHttpsConfigurationProvider @Inject()(c: Configuration, e: Environment)
    extends Provider[RedirectHttpsConfiguration] {
  import util.RedirectHttpsKeys._

  private val logger = Logger(getClass)

  lazy val get: RedirectHttpsConfiguration = {
    val strictTransportSecurity = c.get[Option[String]](stsPath)
    val redirectStatusCode      = c.get[Int](statusCodePath)
    if (!isRedirect(redirectStatusCode)) {
      throw c.reportError(statusCodePath,
                          s"Status Code $redirectStatusCode is not a Redirect status code!")
    }
    val port = c.get[Option[Int]](portPath)
    val redirectEnabled = c.get[Option[Boolean]](redirectEnabledPath).getOrElse {
      if (e.mode != Mode.Prod) {
        logger.info(
          s"RedirectHttpsFilter is disabled by default except in Prod mode.\n" +
            s"See https://www.playframework.com/documentation/2.6.x/RedirectHttpsFilter"
        )
      }
      e.mode == Mode.Prod
    }

    RedirectHttpsConfiguration(strictTransportSecurity, redirectStatusCode, port, redirectEnabled)
  }
}

class HerokuRedirectHttpsModule
    extends SimpleModule(
      bind[RedirectHttpsConfiguration].toProvider[RedirectHttpsConfigurationProvider],
      bind[HerokuRedirectHttpsFilter].toSelf
    )

trait HerokuRedirectHttpsComponents {
  def configuration: Configuration
  def environment: Environment

  lazy val redirectHttpsConfiguration: RedirectHttpsConfiguration =
    new RedirectHttpsConfigurationProvider(configuration, environment).get
  lazy val herokuRedirectHttpsFilter: HerokuRedirectHttpsFilter =
    new HerokuRedirectHttpsFilter(redirectHttpsConfiguration)
}
