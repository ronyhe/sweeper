package com.ronyhe.sweeper.model

import com.ronyhe.sweeper.Coord
import com.ronyhe.sweeper.model.Board.MineCell
import org.scalatest.{FunSuite, PrivateMethodTester}

class BoardTest extends FunSuite with PrivateMethodTester {

  def allCoords(board: Board) = for (r <- 0 until board.rows; c <- 0 until board.cols) yield r -> c

  test("board contains correct amount of mines") {
    val board = new Board(16, 30, 99, 0->0)
    val coords = allCoords(board)
    val mines = coords.count(board(_).isInstanceOf[MineCell])
    assert(99 === mines)
  }

  test("adjacent mine values are correct") {
    val board = new Board(16, 30, 99, 0->0)
    val coords = allCoords(board)
    for (coord <- coords) {
      val cell = board(coord)
      if (!cell.isInstanceOf[MineCell]) {
        val neighbors = board.adjacentCoords(coord)
        val cells = neighbors map board.apply
        val mines = cells count (_.isInstanceOf[MineCell])
        assert(mines === cell.adjacentMines, s"$coord\n$board")
      }
    }
  }

  test("adjacentCoords functions properly") {
    val board = new Board(10, 10, 4, 0 -> 0)
    val func = PrivateMethod[Seq[Coord]]('adjacentCoords)
    def adjacent(to: Coord) = board invokePrivate func(to)

    assert(Seq( 0 -> 1, 1 -> 0, 1 -> 1 ) === adjacent( 0 -> 0 ), "top left corner adjacency is incorrect")
    assert(Seq( 0 -> 4, 0 -> 6, 1 -> 4, 1 -> 5, 1 -> 6 ) === adjacent( 0 -> 5 ), "top row adjacency is incorrect")
    assert(Seq( 0 -> 8, 1 -> 8, 1 -> 9 ) === adjacent( 0 -> 9 ), "top right corner adjacency is incorrect")

    assert(Seq( 4 -> 0, 4 -> 1, 5 -> 1, 6 -> 0, 6 -> 1 )  === adjacent( 5 -> 0 ), "left col adjacency is incorrect")
    assert(Seq(
      4 -> 4, 4 -> 5, 4 -> 6,
      5 -> 4,         5 -> 6,
      6 -> 4, 6 -> 5, 6 -> 6
    )  === adjacent( 5 -> 5 ), "center adjacency is incorrect")
    assert( Seq( 4 -> 8, 4 -> 9, 5 -> 8, 6 -> 8, 6 -> 9 ) === adjacent( 5 -> 9 ), "right col adjacency is incorrect")

    assert( Seq(8 -> 0, 8 -> 1, 9 -> 1) === adjacent( 9 -> 0 ), "bottom left corner adjacency is incorrect" )
    assert( Seq(8 -> 4, 8 -> 5, 8 -> 6, 9 -> 4, 9 -> 6) === adjacent( 9 -> 5 ), "bottom row adjacency is incorrect" )
    assert( Seq(8 -> 8, 8 -> 9, 9 -> 8) === adjacent( 9 -> 9 ), "bottom right corner adjacency is incorrect" )
  }

}
