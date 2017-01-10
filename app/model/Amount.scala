package model

case class Amount(pence: Int) {

  val isPos: Boolean = pence > 0
  val isNeg: Boolean = pence < 0

  def neg: Amount = Amount(-pence)

  def op(a: Amount)(f: (Int, Int) => Int): Amount = Amount(f(pence, a.pence))

  def plus(a: Amount): Amount = op(a) { _ + _ }
  def minus(a: Amount): Amount = op(a) { _ - _ }

  def pounds: Double = pence.toDouble / 100
  def formatted: String = java.text.NumberFormat.getCurrencyInstance.format(pounds)
}

object Amount {

  def fromString(s: String): Amount = Amount((s.toDouble * 100).toInt)

  def sum(as: Amount*): Amount = as.foldLeft(Amount(0)) { case (acc, b) => acc.plus(b) }

  def abs(a: Amount) = Amount(a.pence.abs)
}
