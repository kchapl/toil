package model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import model.Transaction.Category.uncategorised

object TransactionParser {

  private val dateFormatter = DateTimeFormatter.ofPattern("dd'/'MM'/'yyyy")

  private def stripUnprintableChars(s: String) = s.replaceAll("[^ -~]", "")

  def parseLine(account: String)(
    line: String,
    payeeFrom: Seq[String] => String,
    referenceFrom: Seq[String] => Option[String],
    modeFrom: Seq[String] => Option[String]
  ): Transaction = {
    val parsed = Csv.parse(stripUnprintableChars(line))
    Transaction(
      account,
      date = LocalDate.parse(parsed.head, dateFormatter),
      payee = payeeFrom(parsed).trim,
      reference = referenceFrom(parsed).map(_.trim),
      mode = modeFrom(parsed).map(_.trim),
      amount = Amount.fromString(parsed.last.replaceFirst(",", "")),
      category = uncategorised
    )
  }

  def parseHongCurrLine(account: String)(line: String): Transaction = {
    def description(parsed: Seq[String]) = parsed(1).split("\\s{2,}")
    parseLine(account)(
      line,
      payeeFrom = { parsed => description(parsed).head },
      referenceFrom = { parsed =>
        val desc = description(parsed)
        if (desc.length > 2) Some(desc.drop(1).dropRight(1).mkString(" "))
        else None
      },
      modeFrom = { parsed => Some(description(parsed).last) }
    )
  }

  def parseHongCreditLine(account: String)(line: String): Transaction = {
    def description(parsed: Seq[String]) = parsed(1).splitAt(22)
    parseLine(account)(
      line,
      payeeFrom = { parsed => description(parsed)._1 },
      referenceFrom = { parsed => Some(description(parsed)._2) },
      modeFrom = { _ => None }
    )
  }
}
