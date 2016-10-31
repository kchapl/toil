package models

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object TxParser {

  private val dateFormatter = DateTimeFormatter.ofPattern("dd'/'MM'/'yyyy")

  def parseLine(account: String)(line: String): Transaction = {
    val parsed = Csv.parse(line.replaceAll("[^ -~]", ""))
    val description = parsed(1).split("\\s{2,}")
    Transaction(
      account,
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
