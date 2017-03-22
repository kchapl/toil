package model.time

import java.time.LocalDate

case class YearSeason(year: Int, season: Season) {

  def contains(date: LocalDate): Boolean =
    Season.of(date) == season && (
      if (date.getMonthValue > 2) date.getYear == year else date.getYear == year + 1
    )
}

object YearSeason {

  def from(date: LocalDate): YearSeason = YearSeason(
    year = if (date.getMonthValue > 2) date.getYear else date.getYear - 1,
    season = Season.of(date)
  )
}
