package com.ronyhe.sweeper.controller

import javafx.beans.property.SimpleBooleanProperty

import com.ronyhe.sweeper.Coord
import com.ronyhe.sweeper.controller.GameBoard.GameCell
import com.ronyhe.sweeper.model.Board
import com.ronyhe.sweeper.model.Board.MineCell


class GameBoard(board: Board, onExplosion: () => Any) {

  def cell(coord: Coord): GameCell = cells(coord)

  private val cells = Map(allCoords map { coord =>
    val (isMine, adjacentMines) = board(coord) match {
      case c: MineCell => (true, c.adjacentMines)
      case other => (false, other.adjacentMines)
    }

    val adjacentCells = () => board.adjacentCoords(coord).toSet.map(cell)
    coord -> new GameCell(isMine, adjacentMines, adjacentCells, onExplosion)
  }: _*)

  private lazy val allCoords = {
    val rowRange = 0 until board.rows
    val colRange = 0 until board.cols
    for (row <- rowRange; col <- colRange) yield row -> col
  }

}

object GameBoard {
  
  class GameCell(val isMine: Boolean,
                 val adjacentMines: Int,
                 adjacentCells: () => Set[GameCell],
                 onExplosion: () => Any) {

    object Properties {
      val exposed = new SimpleBooleanProperty(false)
      val flagged = new SimpleBooleanProperty(false)
    }

    import Properties._

    def requestFlagging() =
      if (flaggable) flag()

    def requestExposure(): Unit =
      if (exposable) expose()

    def requestExposureOfAdjacentCells() =
      if (exposureOfAdjacentCellsIsAvailable)
        exposeAdjacentCells()


    private def flaggable =
      !exposed.get

    private def flag() =
      flagged.set(true)


    private def exposable =
      !flagged.get && !exposed.get

    private def expose() = {
      exposed.set(true)
      if (isMine)
        onExplosion()
      else if (adjacentMines == 0)
        exposeAdjacentCells()
    }


    private def exposureOfAdjacentCellsIsAvailable = {
      lazy val numAdjacentFlags = adjacentCells() count (_.Properties.flagged.get)
      lazy val allMinesAreAccountedFor = numAdjacentFlags == adjacentMines
      exposed.get && allMinesAreAccountedFor
    }

    private def exposeAdjacentCells() =
      adjacentCells() foreach (_.requestExposure())

  }

}
