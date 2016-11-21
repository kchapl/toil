package models

import java.net.URLEncoder

import scala.util.Properties

object Config {
  val plainClientId = Properties.envOrNone("CLIENT_ID")
  val plainClientSecret = Properties.envOrNone("CLIENT_SECRET")
  val plainRedirectUri = Properties.envOrNone("REDIRECT_URI")
  val sheetFileId = Properties.envOrNone("SHEET_FILE_ID")

  val clientId = URLEncoder.encode(Config.plainClientId.get, "UTF-8")
  val clientSecret = URLEncoder.encode(Config.plainClientSecret.get, "UTF-8")
  val redirectUri = Config.plainRedirectUri.get
}
