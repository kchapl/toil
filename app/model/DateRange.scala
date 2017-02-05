package model

import java.time.LocalDate

import scala.annotation.tailrec

case class DateRange(from: LocalDate, to: LocalDate) {

  def contains(d: LocalDate): Boolean =
    (d.isEqual(from) || d.isAfter(from)) && (d.isEqual(to) || d.isBefore(to))

  lazy val months: Seq[DateRange] = {

    @tailrec
    def go(soFar: Seq[DateRange], firstDayOfMonth: LocalDate): Seq[DateRange] = {
      val nextMonth = firstDayOfMonth.plusMonths(1).minusDays(1)
      if (to.isAfter(nextMonth)) {
        go(soFar :+ DateRange(firstDayOfMonth, nextMonth), firstDayOfMonth.plusMonths(1))
      } else if (to.isEqual(nextMonth)) {
        soFar :+ DateRange(firstDayOfMonth, nextMonth)
      } else soFar
    }

    go(
      soFar = Nil,
      firstDayOfMonth =
        if (from.getDayOfMonth == 1) from.withDayOfMonth(1)
        else from.withDayOfMonth(1).plusMonths(1)
    )
  }
}
