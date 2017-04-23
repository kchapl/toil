package model

case class Amount(pence: Int) {

  private def op(a: Amount)(f: (Int, Int) => Int): Amount = Amount(f(pence, a.pence))

  def plus(a: Amount): Amount = op(a) { _ + _ }
  def minus(a: Amount): Amount = op(a) { _ - _ }

  def pounds: Double = (BigDecimal(pence) / 100).toDouble
  def formatted: String = java.text.NumberFormat.getCurrencyInstance.format(pounds)
}

object Amount {

  val zero = Amount(0)

  def fromString(s: String): Amount = Amount((BigDecimal(s) * 100).toInt)

  def sum(as: Seq[Amount]): Amount = as.foldLeft(Amount(0)) { case (acc, b) => acc.plus(b) }
}
