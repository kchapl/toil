package model

sealed trait AccountType { def name: String }

case object Current extends AccountType {
  override def name = "Current"
}

case object Credit extends AccountType {
  override def name = "Credit"
}

object AccountType {
  def fromName(name: String): AccountType = name match {
    case n if n == Current.name => Current
    case n if n == Credit.name => Credit
  }
}
