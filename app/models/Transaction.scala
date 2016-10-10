package models

import java.util.Date

case class Transaction(
  date: Date,
  description: String,
  amount: Double
)
