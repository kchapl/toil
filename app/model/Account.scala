package model

case class Account(name: String, accType: AccountType, originalBalance: Amount)

object Account {

  def fromRow(r: Row): Account = Account(
    name = r.head,
    accType = AccountType.fromName(r(1)),
    originalBalance = Amount.fromString(r(2))
  )

  def byName(name: String)(a: Account): Boolean = a.name.toLowerCase == name.toLowerCase
}
