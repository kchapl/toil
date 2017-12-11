package model

sealed trait AccountType { def name: String }

case object Current extends AccountType {
  override val name = "Current"
}

case object Credit extends AccountType {
  override val name = "Credit"
}

case object Savings extends AccountType {
  override val name = "Savings"
}

case object Loan extends AccountType {
  override val name = "Loan"
}

object AccountType {
  def fromName(name: String): AccountType = name match {
    case Current.name => Current
    case Credit.name  => Credit
    case Savings.name => Savings
    case Loan.name    => Loan
  }
}
