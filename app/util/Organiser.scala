package util

import java.time.LocalDate

import model.Transaction

object Organiser {

  implicit val dateOrdering = new Ordering[LocalDate] {
    override def compare(x: LocalDate, y: LocalDate): Int = x.compareTo(y)
  }

  private val filterKey = "fk"
  private val filterValue = "fv"
  private val filterOp = "fo"
  private val sortOrder = "so"

  private val accountField = "ac"
  private val dateField = "da"

  def organise(
    txs: Seq[Transaction],
    params: Map[String, Seq[String]]
  ): Seq[Transaction] = {
    def param(paramName: String) = params.getOrElse(paramName, Nil)
    applySorts(
      applyFilters(txs, param(filterKey), param(filterValue), param(filterOp)),
      param(sortOrder)
    )
  }

  def applyFilters(
    txs: Seq[Transaction],
    keys: Seq[String],
    values: Seq[String],
    ops: Seq[String]
  ): Seq[Transaction] = {
    val filters = keys.zip(values).zip(ops)
    filters.foldLeft(txs) { case (acc, ((k, v), o)) => filter(acc, k, v, o) }
  }

  def filter(
    txs: Seq[Transaction],
    key: String,
    value: String,
    op: String
  ): Seq[Transaction] = {
    (key, value, op) match {
      case (`accountField`, accName, "=") =>
        txs.filter(_.account == accName)
      case (`dateField`, date, "<=") =>
        txs.filter(_.date.isBefore(LocalDate.parse(date).plusDays(1)))
      case (`dateField`, date, ">=") =>
        txs.filter(_.date.isAfter(LocalDate.parse(date).minusDays(1)))
      case _ =>
        txs
    }
  }

  def applySorts(
    txs: Seq[Transaction],
    keys: Seq[String]
  ): Seq[Transaction] = {
    keys.foldLeft(txs) { case (acc, k) => sort(acc, k) }
  }

  def sort(txs: Seq[Transaction], key: String): Seq[Transaction] = {
    key match {
      case `dateField` => txs.sortBy(_.date)
      case _ => txs
    }
  }
}
