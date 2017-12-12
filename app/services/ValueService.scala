package services

import com.google.api.client.auth.oauth2.Credential
import com.google.api.services.sheets.v4.model.UpdateValuesResponse
import model.{Row, Sheet}

import scala.util.Try

trait ValueService {

  def allRows(sheet: Sheet, credential: Credential): Seq[Row]

  def appendRows(sheet: Sheet, rows: Seq[Row], credential: Credential): Int

  def replaceAllRows(sheet: Sheet,
                     replacements: Seq[Row],
                     credential: Credential): Either[String, Unit]

  def updateCellValue(
      sheetName: String,
      rowIdx: Int,
      colIdx: Int,
      updateTo: String,
      credential: Credential
  ): Try[UpdateValuesResponse]
}
