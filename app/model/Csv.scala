package model

import scala.annotation.tailrec

object Csv {

  def parse(line: String): Seq[String] = {

    @tailrec
    def go(acc: Seq[String], accPart: String, inQuotes: Boolean, rest: Seq[Char]): Seq[String] = {
      rest match {
        case ',' :: tail if !inQuotes => go(acc :+ accPart, "", inQuotes, tail)
        case '"' :: tail => go(acc, accPart, inQuotes = !inQuotes, tail)
        case ch :: tail => go(acc, accPart + ch, inQuotes, tail)
        case _ => acc :+ accPart
      }
    }

    go(Nil, "", inQuotes = false, line.toList)
  }
}
