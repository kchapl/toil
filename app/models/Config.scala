package models

import scala.util.Properties

object Config {
  val txPath = Properties.envOrNone("TX_PATH")
  val plainClientId = Properties.envOrNone("CLIENT_ID")
  val plainClientSecret = Properties.envOrNone("CLIENT_SECRET")
  val plainRedirectUri = Properties.envOrNone("REDIRECT_URI")
}
