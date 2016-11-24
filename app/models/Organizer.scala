package models

import java.time.LocalDate

object Organizer {

  implicit val dateOrdering = new Ordering[LocalDate] {
    override def compare(x: LocalDate, y: LocalDate): Int = x.compareTo(y)
  }

  def organize(txs: Seq[Transaction], params: Map[String, Seq[String]]): Seq[Transaction] = {
    sort(
      applyFilters(
        txs,
        params.getOrElse("fk", Nil),
        params.getOrElse("fv", Nil),
        params.getOrElse("fo", Nil)
      ), params
    )
  }

  def applyFilters(
    txs: Seq[Transaction],
    keys: Seq[String],
    values: Seq[String],
    ops: Seq[String]
  ): Seq[Transaction] = {
    val filters = keys.zip(values).zip(ops)
    // todo not quite working
    filters.foldLeft(txs) { case (acc,((k, v), o)) =>
      filter(acc, k, v, o)
    }
  }

  def filter(txs: Seq[Transaction], key: String, value: String, op: String): Seq[Transaction] = {
    (key, value, op) match {
      case ("ac", accName, "=") => txs.filter(_.account == accName)
      case ("dt", date, "<=") => txs.filter(_.date.isBefore(LocalDate.parse(date)))
      case ("dt", date, "=>") => txs.filter(_.date.isAfter(LocalDate.parse(date)))
      case _ => txs
    }
  }

  def sort(txs: Seq[Transaction], params: Map[String, Seq[String]]): Seq[Transaction] = {
    params.get("so") map {
      case "da" :: t => txs.sortBy(_.date)
      case _ => txs
    } getOrElse txs
  }
}
