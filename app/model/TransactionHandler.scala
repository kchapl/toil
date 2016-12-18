package model

import javax.inject.Inject

import services.Repository

import scala.concurrent.Future
import scala.io.BufferedSource

class TransactionHandler @Inject()(repo: Repository) {

  def allTransactions(accessToken: String): Future[Set[Transaction]] =
    repo.fetchAllTransactions(accessToken)

  def uploadTransactions(accessToken: String, accountName: String, src: BufferedSource): Unit = {

    val txToAppend = {
      def parse(line: String) = accountName match {
        case "HongCurr" => TransactionParser.parseHongCurrLine(accountName)(line)
        case "HongCredit" => TransactionParser.parseHongCreditLine(accountName)(line)
      }
      src.getLines().toSet map parse
    }

    repo.insertTransactions(accessToken, txToAppend)
  }
}
