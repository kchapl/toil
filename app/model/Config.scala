package model

import java.io.File
import java.net.URLEncoder

import scala.util.Properties

object Config {

  def property(key: String): String =
    Properties.envOrNone(key).getOrElse(throw new RuntimeException(s"Missing property '$key'"))

  val plainClientId: String = property("CLIENT_ID")
  val plainClientSecret: String = property("CLIENT_SECRET")
  val redirectUri: String = property("REDIRECT_URI")
  val sheetFileId: String = property("SHEET_FILE_ID")

  val clientId: String = URLEncoder.encode(plainClientId, "UTF-8")
  val clientSecret: String = URLEncoder.encode(plainClientSecret, "UTF-8")

  val appName: String = "Toil"

  val fileStore = new File(Properties.tmpDir)
}
