package model

import java.time.{LocalDate, ZoneId}

import controllers.TransactionBinding
import model.Account.byName
import util.Failure
import util.Util.asOption

import scala.annotation.tailrec
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

  override def equals(obj: Any): Boolean = obj match {
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

  val isTransfer: Boolean      = category == Transfer
  val isIncome: Boolean        = category == Income
  val isSpend: Boolean         = category == Spend
  val isRepayment: Boolean     = category == Repayment
  val isRefund: Boolean        = category == Refund
  val isUncategorised: Boolean = category == Uncategorised
}

object Transaction {

  val categoryColumnIndex = 6

  def parsed(account: Account, source: Source): Set[Transaction] = {
    def parse(line: String) = account.accType match {
      case Current => TransactionParser.parseCurrentLine(account.name)(line)
      case Credit  => TransactionParser.parseCreditLine(account.name)(line)
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

  def indexFromHashcode(hashCode: Int)(implicit refs: Seq[Transaction]): Option[Int] =
    refs.indexWhere(_.hashCode == hashCode) match {
      case -1  => None
      case idx => Some(idx)
    }

  def fromBinding(b: TransactionBinding) = Transaction(
    account = b.account,
    date = b.date.toInstant.atZone(ZoneId.systemDefault).toLocalDate,
    payee = b.payee,
    reference = b.reference,
    mode = b.mode,
    amount = Amount.fromString(b.amount),
    category = Category.fromCode(b.category)
  )

  def fromRow(r: Row) = Transaction(
    account = r.head,
    date = LocalDate.parse(r(1)),
    payee = r(2),
    reference = asOption(r(3)),
    mode = asOption(r(4)),
    amount = Amount.fromString(r(5)),
    category = Category.fromCode(r(6))
  )

  def haveChanged(subset: Set[Transaction])(implicit refs: Set[Transaction]): Boolean =
    subset.exists { t =>
      refs.find(_ == t) exists (_.category != t.category)
    }

  def replace(replacements: Set[Transaction])(implicit refs: Set[Transaction]): Set[Transaction] =
    refs.diff(replacements) ++ replacements

  def findAnomalies(transactions: Seq[Transaction]): Option[Seq[Anomaly]] = {

    val findUnbalancedTransfers: Seq[Anomaly] = {

      @tailrec
      def go(acc: Seq[Anomaly], rest: List[Transaction]): Seq[Anomaly] = {
        rest match {
          case hd :: tl =>
            tl find (_.amount.neg == hd.amount) match {
              case Some(t) => go(acc, tl.filterNot(_ == t))
              case None    => go(acc :+ UnbalancedTransfer(hd), tl)
            }
          case Nil => acc
        }
      }

      go(Nil, transactions.filter(_.isTransfer).toList)
    }

    val findNegativeIncome: Seq[Anomaly] = for {
      transaction <- transactions
      if transaction.isIncome && !transaction.amount.isPos
    } yield NegativeIncome(transaction)

    val findNegativeRefunds: Seq[Anomaly] = for {
      transaction <- transactions
      if transaction.isRefund && !transaction.amount.isPos
    } yield NegativeRefund(transaction)

    val findPositiveSpend: Seq[Anomaly] = for {
      transaction <- transactions
      if transaction.isSpend && !transaction.amount.isNeg
    } yield PositiveSpend(transaction)

    val findPositiveRepayments: Seq[Anomaly] = for {
      transaction <- transactions
      if transaction.isRepayment && !transaction.amount.isNeg
    } yield PositiveRepayment(transaction)

    val findUncategorised: Seq[Anomaly] = for {
      transaction <- transactions
      if transaction.isUncategorised
    } yield UncategorisedTransaction(transaction)

    asOption(
      findUnbalancedTransfers ++
        findNegativeIncome ++
        findNegativeRefunds ++
        findPositiveSpend ++
        findPositiveRepayments ++
        findUncategorised
    )
  }
}
