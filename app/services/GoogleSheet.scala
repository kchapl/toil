package services

import java.util.{List => JavaList}

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import play.api.Logger
import util.{Config, Flow}

import scala.collection.JavaConverters._
import scala.util.control.NonFatal

object GoogleSheet {

  private val appName = Config.appName
  private val transport = GoogleNetHttpTransport.newTrustedTransport
  private val jsonFactory = JacksonFactory.getDefaultInstance

  private val sheetFileId = Config.sheetFileId

  private def content(rows: Seq[Row]) = {
    val values: JavaList[JavaList[AnyRef]] = rows.map(_.map(_.asInstanceOf[AnyRef]).asJava).asJava
    new ValueRange().setMajorDimension("ROWS").setValues(values)
  }

  private def valuesService(authFlow: HttpRequestInitializer) = {
    new Sheets.Builder(transport, jsonFactory, authFlow)
      .setApplicationName(appName)
      .build()
      .spreadsheets().values()
  }

  private def values(implicit userId: String) = GoogleSheet.valuesService(
    Flow.readWrite.loadCredential(
      userId
    )
  )

  def allRows(sheet: Sheet)(implicit userId: String): Seq[Row] = {
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

  def appendRows(sheet: Sheet, rows: Seq[Row])(implicit userId: String): Int = {
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

  def replaceAllRows(sheet: Sheet, replacements: Seq[Row])
    (implicit userId: String): Either[String, Unit] = {
    try {
      Right(
        {
          values.clear(sheetFileId, sheet.range, null).execute()
          values.update(sheetFileId, sheet.range, content(replacements))
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
