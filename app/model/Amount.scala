package model

import cats.Monoid

case class Amount(pence: Int) {

  val isPos: Boolean   = pence > 0
  val isNeg: Boolean   = pence < 0
  val isEmpty: Boolean = pence == 0

  private def op(a: Amount)(f: (Int, Int) => Int): Amount = Amount(f(pence, a.pence))

  def plus(a: Amount): Amount  = op(a) { _ + _ }
  def minus(a: Amount): Amount = op(a) { _ - _ }

  lazy val abs: Amount = Amount(pence.abs)
  lazy val neg: Amount = Amount(-pence)

  lazy val pounds: Double    = (BigDecimal(pence) / 100).toDouble
  lazy val formatted: String = java.text.NumberFormat.getCurrencyInstance.format(pounds)
}

object Amount {

  implicit val amountOrder: Ordering[Amount] = (left, right) => left.pence compare right.pence

  implicit val amountAdditionMonoid: Monoid[Amount] = new Monoid[Amount] {
    def empty                         = Amount(0)
    def combine(x: Amount, y: Amount) = Amount(x.pence + y.pence)
  }

  def fromString(s: String): Amount = Amount((BigDecimal(s) * 100).toInt)
}
