package model

object RangeFinder {

  def indexToLetter(idx: Int): Char = (idx + 65).toChar

  def sheet(sheetName: String, numCols: Int): Range = s"$sheetName!A:${indexToLetter(numCols - 1)}"

  def cell(sheetName: String, rowIdx: Int, colIdx: Int): Range =
    s"$sheetName!${indexToLetter(colIdx)}${rowIdx + 1}:${indexToLetter(colIdx)}${rowIdx + 1}"
}
