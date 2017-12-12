package model.time

import java.time.{LocalDate, Year, YearMonth}

import model.Transaction
import model.time.DateHelper._

import scala.annotation.tailrec

case class DateRange(start: LocalDate, end: LocalDate)

object DateRange extends App {

  private def nextMonth(date: LocalDate): DateRange =
    DateRange(firstDayOfNextMonth(date), lastDayOfNextMonth(date))

  private def nextSeason(date: LocalDate): DateRange =
    DateRange(firstDayOfNextSeason(date), lastDayOfNextSeason(date))

  private def nextYear(date: LocalDate): DateRange =
    DateRange(firstDayOfNextYear(date), lastDayOfNextYear(date))

  private def forDates(dates: Seq[LocalDate]): DateRange =
    DateRange(dates.minBy(_.toEpochDay), dates.maxBy(_.toEpochDay))

  def forTransactions(transactions: Seq[Transaction]): DateRange =
    forDates(transactions.map(_.date))

  private def groupBy(dateRange: DateRange)(nextPeriod: LocalDate => DateRange): Seq[DateRange] = {

    @tailrec
    def go(acc: Seq[DateRange], curr: DateRange): Seq[DateRange] =
      if (curr.end.isBefore(dateRange.end))
        go(acc :+ curr, nextPeriod(curr.start))
      else
        acc :+ DateRange(curr.start, dateRange.end)

    go(Nil, DateRange(dateRange.start, nextPeriod(dateRange.start).start.minusDays(1)))
  }

  def groupByMonth(dateRange: DateRange): Seq[YearMonth] =
    groupBy(dateRange)(nextPeriod = nextMonth).map(range => YearMonth.from(range.start))

  def groupBySeason(dateRange: DateRange): Seq[YearSeason] =
    groupBy(dateRange)(nextPeriod = nextSeason).map(range => YearSeason.from(range.start))

  def groupByYear(dateRange: DateRange): Seq[Year] =
    groupBy(dateRange)(nextPeriod = nextYear).map(range => Year.from(range.start))
}
