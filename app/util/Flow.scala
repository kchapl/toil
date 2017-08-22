package util

import java.io.File

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.SheetsScopes.{SPREADSHEETS, SPREADSHEETS_READONLY}

import scala.collection.JavaConverters._

class Flow(clientId: String, clientSecret: String, fileStore: File) {

  private val builder =
    new GoogleAuthorizationCodeFlow.Builder(
      GoogleNetHttpTransport.newTrustedTransport,
      JacksonFactory.getDefaultInstance,
      clientId,
      clientSecret,
      Seq(SPREADSHEETS_READONLY).asJava
    )

  val readOnly: GoogleAuthorizationCodeFlow = builder
    .setDataStoreFactory(new FileDataStoreFactory(fileStore))
    .build()

  val readWrite: GoogleAuthorizationCodeFlow = builder
    .setScopes(Seq(SPREADSHEETS).asJava)
    .setDataStoreFactory(new FileDataStoreFactory(fileStore))
    .build()
}
