package services

import java.io.File

import models.Transaction
import org.apache.pdfbox.io.RandomAccessFile
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import scala.collection.JavaConverters._

object TxImporter {

  def importTransactions(path: String): Seq[Transaction] = {

    val parser = new PDFParser(new RandomAccessFile(new File(path), "r"))
    parser.parse()

    val cosDoc = parser.getDocument

    val pdDoc = new PDDocument(cosDoc)
//    pdDoc.getDocumentCatalog.getDocumentOutline.toString

    val pages = pdDoc.getPages

    val page = pages.get(0)

    val entrySet = page.getCOSObject.entrySet()
//    entrySet.asScala.seq.foreach{k => println(s"${k.getKey.getName} = ${k.getValue.getCOSObject}")}

    val pdfStripper = new PDFTextStripper()
    pdfStripper.setStartPage(1)
    pdfStripper.setEndPage(pdDoc.getNumberOfPages)
    pdfStripper.setLineSeparator("\n\n")

    val text = pdfStripper.getText(pdDoc)

    println("--- start ---")
    println(text)
    println("--- end ---")

    Nil
  }
}
