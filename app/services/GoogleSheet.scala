package services

import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global

object GoogleSheet {

  def fetchRange(ws: WSClient, accessToken: String, sheetId: String, range: String) = {
    ws.url(s"https://sheets.googleapis.com/v4/spreadsheets/$sheetId/values/$range")
      .withHeaders("Authorization" -> s"Bearer $accessToken")
      .get().map { response =>
      response.json
    }
  }
}
