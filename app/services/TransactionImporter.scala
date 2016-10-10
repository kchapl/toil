package services

import java.io.File

import models.Transaction
import org.apache.pdfbox.pdmodel.PDDocument

object TransactionImporter {

  def importTransactions(file: File): Seq[Transaction] = {

    val doc = PDDocument.load(file)

    Nil
  }
}
