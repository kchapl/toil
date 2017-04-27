package model

sealed trait Anomaly {
  def transaction: Transaction
  def reason: String
}

case class UnbalancedTransfer(transaction: Transaction) extends Anomaly {
  override val reason = "Unbalanced transfer"
}

case class NegativeIncome(transaction: Transaction) extends Anomaly {
  override val reason = "Negative income"
}

case class NegativeRefund(transaction: Transaction) extends Anomaly {
  override val reason = "Negative refund"
}

case class PositiveSpend(transaction: Transaction) extends Anomaly {
  override val reason = "Positive spend"
}

case class PositiveRepayment(transaction: Transaction) extends Anomaly {
  override val reason = "Positive repayment"
}

case class UncategorisedTransaction(transaction: Transaction) extends Anomaly {
  override val reason = "Uncategorised"
}
