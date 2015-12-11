package com.ronyhe.sweeper.model

import com.ronyhe.sweeper.Coord
import com.ronyhe.sweeper.model.Board.{Cell, MineCell, MineCellObject}
import com.ronyhe.sweeper.utils.CollectionUtils

import scala.annotation.tailrec
import scala.util.Random

class Board(val rows: Int, val cols: Int, mines:Int, firstClick: Coord) {
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
    val coordsThatNeedToBeUpdated = coordsAdjacentToMines filterNot mineLocations.contains
    val coordsToNumbers = CollectionUtils.itemToAmount(coordsThatNeedToBeUpdated)
    val coordsToCells = coordsToNumbers map { t => t._1 -> NumberCells(t._2) }
    CollectionUtils.tupleMap(coordsToCells)
  }

  def adjacentCoords(to: Coord): Seq[Coord] = {
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
    def loop(created: Set[Coord]): Set[Coord] =
      if (created.size == mines) created
      else randCoord match {
        case `firstClick` => loop(created)
        case other => loop(created + other)
      }

    loop(Set.empty).toSeq
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