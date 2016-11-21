package models

import java.net.URLEncoder

import play.api.mvc.Codec.utf_8

case class SheetRange(
  sheetName: String,
  firstColumn: String,
  lastColumn: String
) {
  val selector = s"$sheetName!$firstColumn:$lastColumn"
  val urlEncodedSelector = URLEncoder.encode(selector, utf_8.charset)
}
