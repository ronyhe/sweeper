package com.ronyhe.sweeper.com.ronyhe.sweeper.model

import com.ronyhe.sweeper.com.ronyhe.sweeper.model.Board.{MineCellObject, Cell, MineCell}
import com.ronyhe.sweeper.utils.CollectionUtils

import scala.annotation.tailrec
import scala.util.Random

class Board(rows: Int, cols: Int, mines:Int, firstClick: Coord) {
  import Board.{NumberCells, illegalIndexMessage}

  def apply(coord: Coord): Cell = {
    val (row, col) = coord

    require(row >= 0 && row < rows,
      illegalIndexMessage(trueForRowFalseForCol = true, row, rows))
    require(col >= 0 && col < cols,
      illegalIndexMessage(trueForRowFalseForCol = false, col, cols))

    cells(coord)
  }

  private val cells: Map[Coord, Cell] = createCells(firstClick)


  private def createCells(firstClick: Coord) = {
    val mineLocations = randomMineLocations

    //noinspection ZeroIndexToHead
    val baseMap = Map[Coord, Cell]() withDefaultValue NumberCells(0)
    val mineCells = mineLocations.map(_ -> MineCellObject)
    val numberCells = bindingsOfCoordsThatAreAdjacentToMines(mineLocations)

    baseMap ++ mineCells ++ numberCells
  }

  private def bindingsOfCoordsThatAreAdjacentToMines(mineLocations: Seq[Coord]) = {
    val coordsAdjacentToMines = mineLocations flatMap adjacentCoords
    val coordsToNumbers = CollectionUtils.itemToAmount(coordsAdjacentToMines)
    val coordsToCells = coordsToNumbers map { t => t._1 -> NumberCells(t._2) }
    CollectionUtils.tupleMap(coordsToCells)
  }

  private def adjacentCoords(to: Coord): Seq[Coord] = {
    val (centerRow, centerCol) = to
    val rowRange = centerRow-1 to centerRow+1
    val colRange = centerCol-1 to centerCol+1

    val allCells = for (row <- rowRange; col <- colRange) yield row -> col
    val validCoord = (coord: Coord) => {
      val (r, c) = coord
      r >= 0 && r < rows &&
      c >= 0 && c < cols
    }
    val onlyValidCells = allCells filter validCoord
    val onlyAdjacent = onlyValidCells filterNot { _ == to }

    onlyAdjacent
  }

  private def randomMineLocations: Seq[Coord] = {
    val random = Random
    def randCoord = random.nextInt(rows) -> random.nextInt(cols)

    @tailrec
    def loop(created: List[Coord], len: Int): Seq[Coord] =
      if (len == mines) created
      else randCoord match {
        case `firstClick` => loop(created, len)
        case other => loop(other :: created, len + 1)
      }

    loop(Nil, 0)
  }

  override def toString: String = {
    def cell(c: Cell) = c match {
      case mine: MineCell => "*"
      case num => num.adjacentMines
    }

    def row(r: Int) = (0 until cols).map(c => cell(apply(r, c))).mkString(" ")
    (0 until rows).map(row).mkString("\n")
  }
}

object Board {

  sealed case class Cell(adjacentMines: Int)
  sealed class MineCell extends Cell(0)
  private object MineCellObject extends MineCell
  val NumberCells = 0 to 8 map Cell

  private def illegalIndexMessage(trueForRowFalseForCol: Boolean, illegalIndex: Int, maxIndex: Int): String = {
    val axis = if (trueForRowFalseForCol) "row" else "col"
    s"Illegal $axis index $illegalIndex. " +
      s"Only valid $axis indexes for this Board are 0 to ${maxIndex-1}"
  }
}