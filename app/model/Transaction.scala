package model

import java.time.LocalDate

case class Transaction(
  account: String,
  date: LocalDate,
  payee: String,
  reference: Option[String],
  mode: Option[String],
  amount: Amount
) {
  val isTransfer = {
    mode.contains("TFR") ||
      (mode.contains("DD") && Transaction.ddTransferPayees.exists(payee.contains)) ||
      Transaction.otherTransferPayees.exists(payee.contains)
  }
  val isTransferFrom = isTransfer && amount.isPos
  val isTransferTo = isTransfer && amount.isNeg
  val isIncome = amount.isPos && !isTransfer
  val isSpend = amount.isNeg && !isTransfer
}

object Transaction {
  val ddTransferPayees = Seq("LOANS", "MONEY", "CREDIT CARD")
  val otherTransferPayees = Seq("DIRECT DEBIT PAYMENT")
}
