package model

import model.time.YearSeason

case class SeasonSurplus(
  yearSeason: YearSeason,
  figures: SurplusFigures
)
