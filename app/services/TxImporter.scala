package services

import scala.io.Source

object TxImporter {

  def importTransactions(path: String): Seq[String] = Source.fromFile(path).getLines().toSeq
}
