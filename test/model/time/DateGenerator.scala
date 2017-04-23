package model.time

import java.time.LocalDate

import org.scalacheck.Shrink.shrink
import org.scalacheck.{Arbitrary, Gen, Shrink}

object DateGenerator {

  private val genDateIn21stCentury: Gen[LocalDate] = Gen.choose(0, 36000L) map { daysToAdd =>
    LocalDate.of(2000, 1, 1).plusDays(daysToAdd)
  }

  implicit lazy val arbDate: Arbitrary[LocalDate] = Arbitrary(genDateIn21stCentury)

  def shrinkDate(implicit s: Shrink[LocalDate]): Shrink[LocalDate] = Shrink { date =>
    for (epochDay <- shrink(date.toEpochDay)) yield LocalDate.ofEpochDay(epochDay)
  }
}
