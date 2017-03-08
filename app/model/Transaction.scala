package model

import java.time.LocalDate

import model.Account.byName
import util.Failure

import scala.io.{BufferedSource, Source}

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

  def parsed(account: Account, source: Source): Set[Transaction] = {
    def parse(line: String) = account.accType match {
      case Current => TransactionParser.parseCurrentLine(account.name)(line)
      case Credit => TransactionParser.parseCreditLine(account.name)(line)
    }
    source.getLines().toSet map parse
  }

  def toImport(before: Set[Transaction],
               accounts: Set[Account],
               accountName: String,
               source: Source): Either[Failure, Set[Transaction]] =
    accounts.find(byName(accountName)) map { a =>
      Right(parsed(a, source) -- before)
    } getOrElse Left(Failure(s"No such account: $accountName"))
}
