package model.time

import java.time.LocalDate

import model.time.DateGenerator._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop._
import org.scalacheck.Shrink.shrink
import org.scalacheck.{Arbitrary, Gen, Shrink}

object DateRangeGenerator {

  private val genDateRangeIn21stCentury: Gen[DateRange] =
    for {
      d1 <- arbitrary[LocalDate]
      d2 <- arbitrary[LocalDate]
      isD1Earlier = d1.compareTo(d2) < 0
      start = if (isD1Earlier) d1 else d2
      end = if (isD1Earlier) d2 else d1
    } yield DateRange(start, end)

  implicit lazy val arbDateRange: Arbitrary[DateRange] = Arbitrary(genDateRangeIn21stCentury)

  implicit def shrinkDateRange(implicit s: Shrink[DateRange]): Shrink[DateRange] = Shrink { dateRange =>
    if (!dateRange.end.isAfter(dateRange.start)) {
      Stream.empty
    } else if (dateRange.end.minusYears(10).isAfter(dateRange.start)) {
      Stream(dateRange.copy(end = dateRange.end.minusYears(10)))
    } else if (dateRange.end.minusYears(1).isAfter(dateRange.start)) {
      Stream(dateRange.copy(end = dateRange.end.minusYears(1)))
    } else if (dateRange.end.minusMonths(1).isAfter(dateRange.start)) {
      Stream(dateRange.copy(end = dateRange.end.minusMonths(1)))
    } else {
      for (shrunk <- shrink(dateRange.end)) yield dateRange.copy(end = shrunk)
    }
  }
}
