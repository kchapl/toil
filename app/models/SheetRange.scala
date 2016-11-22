package models

import java.net.URLEncoder

import play.api.mvc.Codec.utf_8

case class SheetRange(
  sheetName: String,
  fromColumn: String,
  toColumn: String
) {
  val selector = s"$sheetName!$fromColumn:$toColumn"
  val urlEncodedSelector = URLEncoder.encode(selector, utf_8.charset)
}
