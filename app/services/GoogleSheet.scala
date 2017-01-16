package services

import java.util.{List => JavaList}

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import model.{Config, Sheet}
import play.api.Logger

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

object GoogleSheet {

  private val appName = Config.appName
  private val sheetFileId = Config.sheetFileId
  private val transport = GoogleNetHttpTransport.newTrustedTransport
  private val jsonFactory = JacksonFactory.getDefaultInstance

  private def valuesService(accessToken: String) = {
    def sheetsService(accessToken: String) = {
      val credential = new GoogleCredential().setAccessToken(accessToken)
      new Sheets.Builder(transport, jsonFactory, credential)
        .setApplicationName(appName)
        .build()
    }
    sheetsService(accessToken).spreadsheets().values()
  }

  def fetchAllRows(accessToken: String, sheet: Sheet): Seq[Seq[String]] = {
    try {
      val response = valuesService(accessToken)
        .get(sheetFileId, sheet.range)
        .execute()
      val rows = response.getValues
      rows.asScala.map(_.asScala.map(_.toString))
    } catch {
      case NonFatal(e) =>
        Logger.error(s"Failed to fetch from sheet ${ sheet.name }", e)
        Nil
    }
  }

  def appendRows(accessToken: String, sheet: Sheet, rows: Seq[Seq[String]]): Int = {
    val values: JavaList[JavaList[AnyRef]] = rows.map(_.map(_.asInstanceOf[AnyRef]).asJava).asJava
    val content = new ValueRange().setMajorDimension("ROWS").setValues(values)
    try {
      val response = valuesService(accessToken)
        .append(sheetFileId, sheet.range, content)
        .setValueInputOption("RAW")
        .execute()
        .getUpdates
      val numRowsAppended = for {
        updates <- Option(response)
        numUpdates <- Option(updates.getUpdatedRows)
      } yield {
        numUpdates.toInt
      }
      numRowsAppended getOrElse 0
    } catch {
      case NonFatal(e) =>
        Logger.error("Failed to append to transactions sheet", e)
        0
    }
  }
}
