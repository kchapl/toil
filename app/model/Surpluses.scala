package model

import model.time.DateRange
import model.time.DateRange.{groupByMonth, groupBySeason}

case class Surpluses(
  monthly: Seq[MonthSurplus],
  seasonal: Seq[SeasonSurplus]
) {
  def hasUncategorised = monthly.exists { monthSurplus =>
    val uncategorised = monthSurplus.figures.uncategorised
    uncategorised.isPos || uncategorised.isNeg
  }
}

object Surpluses {

  val empty = Surpluses(Nil, Nil)

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
        }
      )
    }
  }
}
