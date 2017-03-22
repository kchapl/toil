package model.time

import java.time.LocalDate

sealed trait Season

object Season {

  case object Winter extends Season
  case object Spring extends Season
  case object Summer extends Season
  case object Autumn extends Season

  def of(date: LocalDate): Season = date.getMonthValue match {
    case m if m < 3 || m == 12 => Winter
    case m if m < 6 => Spring
    case m if m < 9 => Summer
    case m if m < 12 => Autumn
  }
}
