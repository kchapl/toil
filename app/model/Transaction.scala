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

  override def equals(obj: scala.Any): Boolean = obj match {
    case other: Transaction =>
      other.account == account &&
        other.date == date &&
        other.payee == payee &&
        other.reference == reference &&
        other.mode == mode &&
        other.amount == amount
    case _ => false
  }

  override def hashCode(): Int =
    account.hashCode +
      date.hashCode +
      payee.hashCode +
      reference.hashCode +
      mode.hashCode +
      amount.hashCode

  val isTransfer: Boolean = category == "T"
  val isIncome: Boolean = category == "I"
  val isSpend: Boolean = category == "S"
  val isRepayment: Boolean = category == "R"
  val isRefund: Boolean = category == "B"
  val isUncategorised: Boolean = category == Transaction.Category.uncategorised
}

object Transaction {

  object Category {
    val uncategorised = "U"
  }
}
