package models

case class Account(name: String, originalBalance: Amount, transactions: Seq[Transaction]) {

  val balances: Seq[DateBalance] = Nil
}
