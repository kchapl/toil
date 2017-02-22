package model

import scala.io.BufferedSource

object TransactionHandler {

  def parsed(account: Account, src: BufferedSource): Set[Transaction] = {
    def parse(line: String) = account.accType match {
      case Current => TransactionParser.parseCurrentLine(account.name)(line)
      case Credit => TransactionParser.parseCreditLine(account.name)(line)
    }
    src.getLines().toSet map parse
  }

  def transactionsToImport(account: Account, source: BufferedSource)(before: Iterable[Transaction]): Set[Transaction] = {
    parsed(account, source) -- before
  }
}
