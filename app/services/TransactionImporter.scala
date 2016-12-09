package services

import scala.io.Source

object TransactionImporter {

  def importTransactions(path: String): Seq[String] = Source.fromFile(path).getLines().toSeq
}
