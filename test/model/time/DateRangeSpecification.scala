package model.time

import model.time.DateRange._
import model.time.DateRangeGenerator._
import org.scalacheck.Prop._
import org.scalacheck.Properties

object DateRangeSpecification extends Properties("DateRange") {

  private def numMonths(dateRange: DateRange): Int = {
    val period = dateRange.start.withDayOfMonth(1).until(dateRange.end.withDayOfMonth(dateRange.end.lengthOfMonth()))
    period.getYears * 12 + period.getMonths
  }

  property("groupByMonth") = forAll { dateRange: DateRange =>
    val months = groupByMonth(dateRange)
    months.size == numMonths(dateRange) + 1
  }

  property("groupBySeason") = forAll { dateRange: DateRange =>
    val seasons = groupBySeason(dateRange)
    val numSeasons = numMonths(dateRange) / 3
    seasons.size == numSeasons + 1 || seasons.size == numSeasons + 2
  }
}
