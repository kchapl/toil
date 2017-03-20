package model

import java.time.{LocalDate, ZoneId}

import controllers.TransactionBinding
import model.Account.byName
import util.Failure

import scala.io.Source

case class Transaction(
    account: String,
    date: LocalDate,
    payee: String,
    reference: Option[String],
    mode: Option[String],
    amount: Amount,
    category: Category
) {

  override def equals(obj: Any) = obj match {
    case other: Transaction =>
      other.account == account &&
        other.date == date &&
        other.payee == payee &&
        other.reference == reference &&
        other.mode == mode &&
        other.amount == amount
    case _ => false
  }

  override def hashCode() =
    account.hashCode +
      date.hashCode +
      payee.hashCode +
      reference.hashCode +
      mode.hashCode +
      amount.hashCode

  val isTransfer: Boolean = category == Transfer
  val isIncome: Boolean = category == Income
  val isSpend: Boolean = category == Spend
  val isRepayment: Boolean = category == Repayment
  val isRefund: Boolean = category == Refund
  val isUncategorised: Boolean = category == Uncategorised
}

object Transaction {

  def parsed(account: Account, source: Source): Set[Transaction] = {
    def parse(line: String) = account.accType match {
      case Current => TransactionParser.parseCurrentLine(account.name)(line)
      case Credit => TransactionParser.parseCreditLine(account.name)(line)
      case Savings => TransactionParser.parseCurrentLine(account.name)(line)
    }
    source.getLines().toSet map parse
  }

  def toImport(
      before: Set[Transaction],
      accounts: Set[Account],
      accountName: String,
      source: Source
  ): Either[Failure, Set[Transaction]] =
    accounts.find(byName(accountName)) map { a =>
      Right(parsed(a, source) -- before)
    } getOrElse Left(Failure(s"No such account: $accountName"))

  def fromHashcode(ts: Seq[Transaction])(hashCode: Int): Option[Transaction] =
    ts.find(_.hashCode == hashCode)

  def fromBinding(b: TransactionBinding) = Transaction(
    account = b.account,
    date = b.date.toInstant.atZone(ZoneId.systemDefault).toLocalDate,
    payee = b.payee,
    reference = b.reference,
    mode = b.mode,
    amount = Amount.fromString(b.amount),
    category = Category.fromCode(b.category)
  )
}
