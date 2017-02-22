package model

case class Account(name: String, accType: AccountType, originalBalance: Amount)

object Account {
  def byName(name: String)(a: Account): Boolean = a.name.toLowerCase == name.toLowerCase
}
