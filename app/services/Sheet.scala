package services

import model.RangeFinder

case class Sheet(name: String, numCols: Int) {
  val range: Range = RangeFinder.sheet(name, numCols)
}
