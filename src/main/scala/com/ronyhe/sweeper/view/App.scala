package com.ronyhe.sweeper.view

import javafx.application.Application
import javafx.scene.control.Button
import javafx.scene.{Group, Scene}
import javafx.stage.Stage

import com.ronyhe.sweeper.controller.GameBoard
import com.ronyhe.sweeper.model.Board


class App extends Application {
  val CellSide = 17
  val Rows = 16
  val Cols = 30
  val Mines = 99
  val Width = Cols * CellSide
  val Height = Rows * CellSide

  val gameBoard = {
    val modelBoard = new Board(Rows, Cols, Mines, 0->0)
    new GameBoard(modelBoard, () => println("BOOM"))
  }

  override def start(primaryStage: Stage): Unit = {
    val scene = new Scene(cells, Width, Height)
    primaryStage.setScene(scene)
    primaryStage.setTitle("Sweeper")
    primaryStage.show()
  }

  private val cells = {
    val generator = for {
      row <- 0 until Rows
      col <- 0 until Cols
    } yield (row, col)

    val group = new Group(generator map cell: _*)

    group
  }

  private def cell(coord: (Int, Int)): Button = new CellButton(CellSide, coord, gameBoard.cell(coord))
}

object App {

  def main(args: Array[String]) {
    Application.launch(classOf[App], args: _*)
  }
}
