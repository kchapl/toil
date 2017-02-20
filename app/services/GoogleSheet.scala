package services

import java.util.{List => JavaList}

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import model.{Config, Flow, Sheet}
import play.api.Logger

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

case class GoogleSheet(userId: String) {

  private val sheetFileId = Config.sheetFileId

  private def content(rows: Seq[Seq[String]]) = {
    val values: JavaList[JavaList[AnyRef]] = rows.map(_.map(_.asInstanceOf[AnyRef]).asJava).asJava
    new ValueRange().setMajorDimension("ROWS").setValues(values)
  }

  private val values = GoogleSheet.valuesService(Flow.readWrite.loadCredential(userId))

  def fetchAllRows(sheet: Sheet): Seq[Seq[String]] = {
    try {
      val response = values.get(sheetFileId, sheet.range).execute()
      val rows = response.getValues
      rows.asScala.map(_.asScala.map(_.toString))
    } catch {
      case NonFatal(e) =>
        Logger.error(s"Failed to fetch from sheet ${ sheet.name }", e)
        Nil
    }
  }

  def appendRows(sheet: Sheet, rows: Seq[Seq[String]]): Int = {
    try {
      val response = values.append(sheetFileId, sheet.range, content(rows))
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

  def replaceAllWith(
    sheet: Sheet,
    numRowsToReplace: Int,
    rows: Seq[Seq[String]]
  ): Either[String, Unit] = {
    try {
      Right(
        {
          values.clear(sheetFileId, sheet.range, null).execute()
          values.update(sheetFileId, sheet.range, content(rows))
            .setValueInputOption("RAW")
            .execute()
        }
      )
    } catch {
      case NonFatal(e) =>
        Left(s"Failed to update transactions sheet: ${ e.getMessage }")
    }
  }
}

object GoogleSheet {

  private val appName = Config.appName
  private val transport = GoogleNetHttpTransport.newTrustedTransport
  private val jsonFactory = JacksonFactory.getDefaultInstance

  def valuesService(authFlow: HttpRequestInitializer): Sheets#Spreadsheets#Values = {
    new Sheets.Builder(transport, jsonFactory, authFlow)
      .setApplicationName(appName)
      .build()
      .spreadsheets().values()
  }
}
