package com.example.AP1_Jv_T05B.unit.domain.service;

import static com.example.AP1_Jv_T05B.domain.util.GameConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.AP1_Jv_T05B.domain.entity.Game;
import com.example.AP1_Jv_T05B.domain.entity.GameFieldInterface;
import com.example.AP1_Jv_T05B.domain.service.ComputerMoveCalculator;
import com.example.AP1_Jv_T05B.domain.service.GameLogicService;
import com.example.AP1_Jv_T05B.domain.service.GameStatus;
import java.awt.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameLogicServiceTest {

  @Mock private ComputerMoveCalculator calculator;

  @Mock private Game game;

  @Mock private GameFieldInterface field;

  private GameLogicService service;

  @BeforeEach
  void setUp() {
    service = new GameLogicService(calculator);
    when(game.getGameField()).thenReturn(field);
  }

  // ---------- PvP ----------

  @Test
  void playerXWins_Row() {
    int[][] board = {
      {PLAYER_X_MARK, PLAYER_X_MARK, PLAYER_X_MARK},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
    };

    when(field.getField()).thenReturn(board);
    when(game.isVsComputer()).thenReturn(false);

    GameStatus status = service.isGameOver(game);

    assertEquals(GameStatus.PLAYER_X_WON, status);
  }

  @Test
  void playerOWins_Column() {
    int[][] board = {
      {PLAYER_O_MARK, EMPTY_CELL, EMPTY_CELL},
      {PLAYER_O_MARK, EMPTY_CELL, EMPTY_CELL},
      {PLAYER_O_MARK, EMPTY_CELL, EMPTY_CELL}
    };

    when(field.getField()).thenReturn(board);
    when(game.isVsComputer()).thenReturn(false);

    GameStatus status = service.isGameOver(game);

    assertEquals(GameStatus.PLAYER_O_WON, status);
  }

  // ---------- PvE ----------

  @Test
  void playerWinsAgainstComputer() {
    int[][] board = {
      {PLAYER_MARK, PLAYER_MARK, PLAYER_MARK},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
    };

    when(field.getField()).thenReturn(board);
    when(game.isVsComputer()).thenReturn(true);

    GameStatus status = service.isGameOver(game);

    assertEquals(GameStatus.PLAYER_X_WON, status);
  }

  @Test
  void computerWins() {
    int[][] board = {
      {COMPUTER_MARK, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, COMPUTER_MARK, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, COMPUTER_MARK}
    };

    when(field.getField()).thenReturn(board);
    when(game.isVsComputer()).thenReturn(true);

    GameStatus status = service.isGameOver(game);

    assertEquals(GameStatus.COMPUTER_WON, status);
  }

  // ---------- DRAW / IN PROGRESS ----------

  @Test
  void drawWhenBoardIsFullAndNoWinner() {
    int[][] board = {
      {PLAYER_X_MARK, PLAYER_O_MARK, PLAYER_X_MARK},
      {PLAYER_O_MARK, PLAYER_X_MARK, PLAYER_O_MARK},
      {PLAYER_O_MARK, PLAYER_X_MARK, PLAYER_O_MARK}
    };

    when(field.getField()).thenReturn(board);

    GameStatus status = service.isGameOver(game);

    assertEquals(GameStatus.DRAW, status);
  }

  @Test
  void gameInProgressWhenEmptyCellsExist() {
    int[][] board = {
      {PLAYER_X_MARK, EMPTY_CELL, PLAYER_O_MARK},
      {EMPTY_CELL, PLAYER_X_MARK, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
    };

    when(field.getField()).thenReturn(board);

    GameStatus status = service.isGameOver(game);

    assertEquals(GameStatus.IN_PROGRESS, status);
  }

  // ---------- COMPUTER MOVE ----------

  @Test
  void getNextMoveDelegatesToCalculator() {
    int[][] board = {
      {PLAYER_MARK, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, COMPUTER_MARK, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
    };

    Point expectedMove = new Point(0, 1);

    when(field.getField()).thenReturn(board);
    when(calculator.getBestMove(board)).thenReturn(expectedMove);

    Point move = service.getNextMove(game);

    assertEquals(expectedMove, move);
    verify(calculator).getBestMove(board);
  }
}
