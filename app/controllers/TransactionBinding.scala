package controllers

case class TransactionBinding(
    account: String,
    date: java.util.Date,
    payee: String,
    reference: Option[String],
    mode: Option[String],
    amount: String,
    category: String
)
