package com.example.AP1_Jv_T05B.unit.domain.service;

import static com.example.AP1_Jv_T05B.domain.util.GameConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.AP1_Jv_T05B.domain.service.ComputerMoveCalculator;
import java.awt.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComputerMoveCalculatorTest {

  private ComputerMoveCalculator calculator;

  @BeforeEach
  void setUp() {
    calculator = new ComputerMoveCalculator();
  }

  // ---------- БАЗОВЫЕ СЦЕНАРИИ ----------

  @Test
  void returnsNullWhenBoardIsFull() {
    int[][] board = {
      {PLAYER_MARK, COMPUTER_MARK, PLAYER_MARK},
      {COMPUTER_MARK, PLAYER_MARK, COMPUTER_MARK},
      {COMPUTER_MARK, PLAYER_MARK, COMPUTER_MARK}
    };

    Point move = calculator.getBestMove(board);

    assertNull(move);
  }

  @Test
  void moveIsAlwaysInEmptyCell() {
    int[][] board = {
      {PLAYER_MARK, EMPTY_CELL, COMPUTER_MARK},
      {EMPTY_CELL, PLAYER_MARK, EMPTY_CELL},
      {COMPUTER_MARK, EMPTY_CELL, PLAYER_MARK}
    };

    Point move = calculator.getBestMove(board);

    assertNotNull(move);
    assertEquals(EMPTY_CELL, board[move.y][move.x]);
  }

  // ---------- ВЫИГРЫШ КОМПЬЮТЕРА ----------

  @Test
  void choosesWinningMoveWhenAvailable() {
    int[][] board = {
      {COMPUTER_MARK, COMPUTER_MARK, EMPTY_CELL},
      {PLAYER_MARK, PLAYER_MARK, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
    };

    Point move = calculator.getBestMove(board);

    assertEquals(new Point(2, 0), move);
  }

  // ---------- БЛОКИРОВКА ИГРОКА ----------

  @Test
  void blocksPlayerWinningMove() {
    int[][] board = {
      {PLAYER_MARK, PLAYER_MARK, EMPTY_CELL},
      {COMPUTER_MARK, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
    };

    Point move = calculator.getBestMove(board);

    assertEquals(new Point(2, 0), move);
  }

  // ---------- ПЕРВЫЙ ХОД ----------

  @Test
  void returnsAnyValidMoveOnEmptyBoard() {
    int[][] board = {
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
    };

    Point move = calculator.getBestMove(board);

    assertNotNull(move);
    assertTrue(move.x >= 0 && move.x < 3);
    assertTrue(move.y >= 0 && move.y < 3);
  }
}
