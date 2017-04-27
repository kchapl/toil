package model

import model.TransactionGenerator._
import org.scalacheck.Prop._
import org.scalacheck.Properties

object TransactionSpecification extends Properties("Transaction") {

  property("findAnomalies") = forAll { transactions: Seq[Transaction] =>
    val p1 = Transaction.findAnomalies(transactions).isEmpty

    val p2 = transactions.exists { t1 =>
      t1.isTransfer && !transactions.exists { t2 =>
        t2.isTransfer && t2.amount.neg == t1.amount
      }
    }
    val p3 = transactions.exists(t => t.isIncome && !t.amount.isPos)
    val p4 = transactions.exists(t => t.isRefund && !t.amount.isPos)
    val p5 = transactions.exists(t => t.isSpend && !t.amount.isNeg)
    val p6 = transactions.exists(t => t.isRepayment && !t.amount.isNeg)
    val p7 = transactions.exists(_.isUncategorised)

    p1 ^ (p2 || p3 || p4 || p5 || p6 || p7)
  }
}
