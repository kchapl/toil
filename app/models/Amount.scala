package models

case class Amount(pounds: Double) {

  val pence: Int = (pounds * 100).toInt

  val isPos: Boolean = pounds > 0
  val isNeg: Boolean = pounds < 0

  def op(a: Amount)(f: (Int, Int) => Int): Amount = Amount(f(pence, a.pence).toDouble / 100)

  def plus(a: Amount): Amount = op(a) { _ + _ }
  def minus(a: Amount): Amount = op(a) { _ - _ }

  def formatted: String = java.text.NumberFormat.getCurrencyInstance.format(pounds)
}

object Amount {

  def apply(s: String): Amount = Amount(s.toDouble)

  def sum(as: Seq[Amount]): Amount = as.foldLeft(Amount(0)) { case (acc, b) => acc.plus(b) }

  def abs(a: Amount) = Amount(a.pounds.abs)
}
