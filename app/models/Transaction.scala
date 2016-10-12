package models

import java.time.LocalDate

case class Transaction(
  date: LocalDate,
  payee: String,
  reference: Option[String],
  mode: String,
  amount: Double
)
