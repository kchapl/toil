package model

import model.time.DateRange
import model.time.DateRange.{groupByMonth, groupBySeason, groupByYear}

case class Surpluses(
    monthly: Seq[MonthSurplus],
    seasonal: Seq[SeasonSurplus],
    yearly: Seq[YearSurplus]
) {
  def hasUncategorised: Boolean = monthly.exists(!_.figures.uncategorised.isEmpty)
}

object Surpluses {

  val empty = Surpluses(Nil, Nil, Nil)

  def fromTransactions(transactions: Seq[Transaction]): Surpluses = {
    if (transactions.isEmpty) {
      Surpluses.empty
    } else {
      val range = DateRange.forTransactions(transactions)
      Surpluses(
        monthly = {
          groupByMonth(range) map { month =>
            MonthSurplus(
              yearMonth = month,
              figures = SurplusFigures.fromTransactions(
                transactions filterNot { t =>
                  t.date.isBefore(month.atDay(1)) || t.date.isAfter(month.atEndOfMonth())
                }
              )
            )
          }
        },
        seasonal = {
          groupBySeason(range) map { season =>
            SeasonSurplus(
              yearSeason = season,
              figures = SurplusFigures.fromTransactions(
                transactions filter (t => season.contains(t.date))
              )
            )
          }
        },
        yearly = {
          groupByYear(range) map { year =>
            YearSurplus(
              year = year,
              figures = SurplusFigures.fromTransactions(
                transactions filterNot { t =>
                  t.date.isBefore(year.atDay(1)) || t.date.isAfter(
                    year.atDay(1).plusYears(1).minusDays(1))
                }
              )
            )
          }
        }
      )
    }
  }
}
