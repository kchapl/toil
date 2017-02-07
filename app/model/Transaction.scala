package model

import java.time.LocalDate

case class Transaction(
  account: String,
  date: LocalDate,
  payee: String,
  reference: Option[String],
  mode: Option[String],
  amount: Amount,
  category: String
) {
  val isTransfer: Boolean = category == "T"
  val isIncome: Boolean = category == "I"
  val isSpend: Boolean = category == "S"
  val isRepayment: Boolean = category == "R"
  val isRefund: Boolean = category == "B"
  val isUncategorised: Boolean = category == "U"
}
