package com.ronyhe.sweeper.view

import java.lang.Boolean
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.scene.text.Font

import com.ronyhe.sweeper.Coord
import com.ronyhe.sweeper.controller.GameBoard
import GameBoard.GameCell

class CellButton(cellSide: Int, coord: Coord, gameCell: GameCell) extends Button {
  val (row, col) = coord

  specifyHeight()
  specifyWidth()
  specifyPosition()
  specifyStyle()
  specifyBehaviour()


  private def specifyBehaviour(): Unit = {
    addPropertyListeners()
    addEventHandlers()
  }

  private def addEventHandlers(): Unit = {
    setOnMousePressed(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = handleMouseEvent(event)
    })
    setOnMouseReleased(new EventHandler[MouseEvent] {
      override def handle(event: MouseEvent): Unit = handleMouseEvent(event)
    })
  }

  private def addPropertyListeners(): Unit = {
    gameCell.Properties.exposed.addListener(new ChangeListener[Boolean] {
      override def changed(observable: ObservableValue[_ <: Boolean], oldValue: Boolean, newValue: Boolean): Unit = {
        if (gameCell.isMine)
          setText("*")
        else {
          setText(gameCell.adjacentMines.toString)
        }
      }
    })

    gameCell.Properties.flagged.addListener(new ChangeListener[Boolean] {
      override def changed(observable: ObservableValue[_ <: Boolean], oldValue: Boolean, newValue: Boolean): Unit = {
        if (newValue) setText("F") else setText("")
      }
    })
  }

  private def handleMouseEvent(mouseEvent: MouseEvent): Unit = {
    import MouseEvent._
    import javafx.scene.input.MouseButton._

    val event = mouseEvent.getEventType
    val button = mouseEvent.getButton

    val otherButtonIsDown = button match {
      case PRIMARY => mouseEvent.isSecondaryButtonDown
      case SECONDARY => mouseEvent.isPrimaryButtonDown
      case _ => return
    }

    (event, button, otherButtonIsDown) match {
      case (MOUSE_PRESSED, SECONDARY, _) => gameCell.requestFlagging()
      case (MOUSE_RELEASED, _, true) => gameCell.requestExposureOfAdjacentCells()
      case (MOUSE_RELEASED, PRIMARY, _) => gameCell.requestExposure()
      case _ =>
    }

  }

  private def specifyWidth() = {
    setMinWidth(cellSide)
    setPrefWidth(cellSide)
    setMaxWidth(cellSide)
  }

  private def specifyHeight() = {
    setMinHeight(cellSide)
    setPrefHeight(cellSide)
    setMaxHeight(cellSide)
  }

  private def specifyPosition() = {
    setLayoutX(col * cellSide)
    setLayoutY(row * cellSide)
  }

  private def specifyStyle() = {
    setFont(CellButton.Font)
    setFocusTraversable(false)
    setStyle("-fx-background-radius: 0")
  }
}

object CellButton {
  val Font = new Font(7)
}


