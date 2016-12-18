package model

case class Amount(pounds: Double) {

  val pence: Int = (pounds * 100).toInt

  val isPos: Boolean = pounds > 0
  val isNeg: Boolean = pounds < 0

  def neg: Amount = Amount(-pounds)

  def op(a: Amount)(f: (Int, Int) => Int): Amount = Amount(f(pence, a.pence).toDouble / 100)

  def plus(a: Amount): Amount = op(a) { _ + _ }
  def minus(a: Amount): Amount = op(a) { _ - _ }

  def formatted: String = java.text.NumberFormat.getCurrencyInstance.format(pounds)
  override def toString: String = pounds.toString
}

object Amount {

  def apply(s: String): Amount = Amount(s.toDouble)

  def sum(as: Amount*): Amount = as.foldLeft(Amount(0)) { case (acc, b) => acc.plus(b) }
  def sum(as: Set[Amount]): Amount = sum(as.toSeq:_*)

  def abs(a: Amount) = Amount(a.pounds.abs)
}
