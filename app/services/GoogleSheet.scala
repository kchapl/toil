package services

import play.api.http.Status.OK
import play.api.libs.json._
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object GoogleSheet {

  case class Row(values: Seq[String])

  // https://developers.google.com/sheets/reference/rest/v4
  // /spreadsheets.values/get
  def getValues(
    ws: WSClient,
    accessToken: String,
    sheetFileId: String,
    range: String
  ): Future[Either[ServiceException, Seq[Row]]] = {

    def toRows(values: Seq[JsValue]): Seq[Row] =
      values map { case JsArray(v) => toRow(v) }

    def toRow(values: Seq[JsValue]): Row =
      Row(values map { case JsString(u) => u })

    val domain = "sheets.googleapis.com"
    val url = s"https://$domain/v4/spreadsheets/$sheetFileId/values/$range"
    ws.url(url)
      .withHeaders("Authorization" -> s"Bearer $accessToken")
      .get().map { response =>

      val JsDefined(JsArray(jsonRows)) = response.json \ "values"
      response.status match {
        case OK =>
          Right(toRows(jsonRows))
        case _ =>
          Left(ServiceException(response.status, response.statusText))
      }
    }
  }

  // https://developers.google.com/sheets/reference/rest/v4/spreadsheets
  // .values/append
  //  def appendValues(
  //    ws: WSClient,
  //    accessToken: String,
  //    sheetFileId: String,
  //    range: String,
  //    values: Seq[Row]
  //  ): Future[Either[ServiceException, Seq[Row]]] = {
  //    ws.url(s"https://sheets.googleapis
  // .com/v4/spreadsheets/$sheetFileId/values/$range:append")
  //      .withHeaders("Authorization" -> s"Bearer $accessToken")
  //      .post().map { response =>
  //
  //      println(response.json)
  //
  //      response.status match {
  //        case OK => Right(Nil)
  //        case _ => Left(ServiceException(response.status, response
  // .statusText))
  //      }
  //    }
  //  }
}
