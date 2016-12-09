package services

import java.net.URLEncoder

import play.api.http.Status.OK
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSRequest}
import play.api.mvc.Codec.utf_8

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object GoogleSheet {

  case class SheetRange(
    sheetName: String,
    fromColumn: String,
    toColumn: String
  ) {
    val selector = s"$sheetName!$fromColumn:$toColumn"
    val urlEncodedSelector = URLEncoder.encode(selector, utf_8.charset)
  }

  case class Row(values: Seq[String])

  private def mkRequest(
    ws: WSClient,
    accessToken: String,
    sheetFileId: String,
    suffix: String
  ): WSRequest = {
    val url = s"https://sheets.googleapis.com/v4/spreadsheets/$sheetFileId/values/$suffix"
    ws.url(url).withHeaders("Authorization" -> s"Bearer $accessToken")
  }

  /*
   * https://developers.google.com/sheets/reference/rest/v4/spreadsheets.values/get
   */
  def getValues(
    ws: WSClient,
    accessToken: String,
    sheetFileId: String,
    range: SheetRange
  ): Future[Either[String, Seq[Row]]] = {

    def toRows(values: Seq[JsValue]): Seq[Row] =
      values map {
        case JsArray(v) => toRow(v)
        case _ => Row(Nil)
      }

    def toRow(values: Seq[JsValue]): Row =
      Row(
        values map {
          case JsString(u) => u
          case _ => ""
        }
      )

    mkRequest(ws, accessToken, sheetFileId, range.urlEncodedSelector).get().map { response =>

      // todo: turn into service exception and log in controller
      //println(response.body)

      response.status match {
        case OK =>
          response.json \ "values" match {
            case JsDefined(JsArray(jsonRows)) => Right(toRows(jsonRows))
            case _ => Right(Nil)
          }
        case _ => Left(s"${ response.status }: ${ response.statusText }")
      }
    }
  }

  /*
   * https://developers.google.com/sheets/reference/rest/v4/spreadsheets.values/append
   */
  def appendValues(
    ws: WSClient,
    accessToken: String,
    sheetFileId: String,
    range: SheetRange,
    values: Seq[Row]
  ): Future[Either[String, Unit]] = {

    def rowToJson(row: Row) = JsArray(
      Seq(
        JsString(row.values(0)),
        JsString(row.values(1)),
        JsString(row.values(2)),
        JsString(row.values(3)),
        JsString(row.values(4)),
        JsString(row.values(5))
      )
    )

    val valuesToJson =
      JsObject(Seq("values" -> JsArray(values map rowToJson)))

    val suffix = s"${ range.urlEncodedSelector }:append?valueInputOption=raw"
    mkRequest(ws, accessToken, sheetFileId, suffix)
      .post(valuesToJson).map { response =>

      // todo: turn into service exception and log in controller
      //println(response.body)

      response.status match {
        case OK => Right(())
        case _ => Left(s"${ response.status }: ${ response.statusText }")
      }
    }
  }
}
