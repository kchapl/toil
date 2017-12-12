package model.time

import java.time.{LocalDate, YearMonth}

object DateHelper {

  def lastDayOfMonth(date: LocalDate): LocalDate = firstDayOfNextMonth(date).minusDays(1)

  def firstDayOfNextMonth(date: LocalDate): LocalDate = date.withDayOfMonth(1).plusMonths(1)

  def lastDayOfNextMonth(date: LocalDate): LocalDate =
    firstDayOfNextMonth(date).plusMonths(1).minusDays(1)

  private def yearMonthOfNextSeason(date: LocalDate): YearMonth = {
    val month = date.getMonthValue / 3 * 3 + 3
    if (month > 12) YearMonth.of(date.getYear + 1, month - 12)
    else YearMonth.of(date.getYear, month)
  }

  def firstDayOfNextSeason(date: LocalDate): LocalDate = yearMonthOfNextSeason(date).atDay(1)

  def lastDayOfNextSeason(date: LocalDate): LocalDate =
    yearMonthOfNextSeason(date).plusMonths(2).atEndOfMonth

  def firstDayOfNextYear(date: LocalDate): LocalDate = date.withDayOfYear(1).plusYears(1)

  def lastDayOfNextYear(date: LocalDate): LocalDate =
    firstDayOfNextYear(date).plusYears(1).minusDays(1)
}
