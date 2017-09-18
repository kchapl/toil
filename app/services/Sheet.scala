package services

case class Sheet(name: String, numCols: Int) {
  val range: Range = RangeFinder.sheet(name, numCols)
}
