package model

import java.time.LocalDate

import services.GoogleSheet

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

  val transactionSheet = Sheet("Transactions", numCols = 7)

  private def opt(s: String) = if (s.isEmpty) None else Some(s)

  def fromRow(values: Seq[String]): Transaction = Transaction(
    account = values.head,
    date = LocalDate.parse(values(1)),
    payee = values(2),
    reference = opt(values(3)),
    mode = opt(values(4)),
    amount = Amount.fromString(values(5)),
    category = values(6)
  )

  def dedup(userId: String): Either[String, Unit] = {
    val sheet = GoogleSheet(userId)
    val ts = sheet.fetchAllRows(transactionSheet).map(fromRow)
    sheet.replaceAllWith(
      transactionSheet,
      ts.size,
      ts.distinct.map(RowHelper.toRow)
    )
  }
}
