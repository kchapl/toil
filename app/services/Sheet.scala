package services

case class Sheet(name: String, numCols: Int) {
  val range: String = {
    val endColumn = (numCols + 64).toChar.toString
    s"$name!A:$endColumn"
  }
}
