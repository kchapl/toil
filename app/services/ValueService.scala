package services

import com.google.api.client.auth.oauth2.Credential

trait ValueService {

  def appendRows(sheet: Sheet, rows: Seq[Row], credential: Credential): Int

  def replaceAllRows(sheet: Sheet, replacements: Seq[Row], credential: Credential): Either[String, Unit]
}
