package services

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import models.{Csv, Transaction}

import scala.io.Source

object TxImporter {

  private val dateFormatter = DateTimeFormatter.ofPattern("dd'/'MM'/'yyyy")

  def importTransactions(path: String): Seq[Transaction] = Source.fromFile(path).getLines().toSeq map parseLine

  def parseLine(line: String): Transaction = {
    val parsed = Csv.parse(line.replaceAll("[^ -~]", ""))
    val description = parsed(1).split("\\s{2,}")
    Transaction(
      date = LocalDate.parse(parsed.head, dateFormatter),
      payee = description.head,
      reference = {
        if (description.length > 2) Some(description.drop(1).dropRight(1).mkString(" "))
        else None
      },
      mode = description.last,
      amount = parsed.last.replaceFirst(",", "").toDouble
    )
  }
}
