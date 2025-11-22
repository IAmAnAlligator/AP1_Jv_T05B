package com.example.AP1_Jv_T05B.domain.service;

import static com.example.AP1_Jv_T05B.domain.util.GameConstants.COMPUTER_MARK;
import static com.example.AP1_Jv_T05B.domain.util.GameConstants.EMPTY_CELL;
import static com.example.AP1_Jv_T05B.domain.util.GameConstants.PLAYER_MARK;
import static com.example.AP1_Jv_T05B.domain.util.GameConstants.PLAYER_O_MARK;
import static com.example.AP1_Jv_T05B.domain.util.GameConstants.PLAYER_X_MARK;

import com.example.AP1_Jv_T05B.domain.entity.Game;
import com.example.AP1_Jv_T05B.domain.entity.GameFieldInterface;
import java.awt.Point;
import org.springframework.stereotype.Service;

@Service
public class GameLogicService implements GameLogic {

  private final ComputerMoveCalculator calculator;

  public GameLogicService(ComputerMoveCalculator calculator) {
    this.calculator = calculator;
  }

  @Override
  public GameStatus isGameOver(Game game) {
    int[][] board = game.getGameField().getField();

    // Проверка победителя
    GameStatus result = checkWinner(board, game);
    if (result != null) {
      return result;
    }

    // Проверка на ничью
    for (int[] row : board) {
      for (int cell : row) {
        if (cell == EMPTY_CELL) {
          return GameStatus.IN_PROGRESS;
        }
      }
    }

    return GameStatus.DRAW;
  }

  @Override
  public Point getNextMove(Game game) {
    GameFieldInterface field = game.getGameField();
    return calculator.getBestMove(field.getField());
  }

  // --- приватные методы ---
  private GameStatus checkWinner(int[][] board, Game game) {
    for (int i = 0; i < 3; i++) {
      // Строки
      if (board[i][0] != EMPTY_CELL && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
        return getWinnerStatus(board[i][0], game);
      }
      // Столбцы
      if (board[0][i] != EMPTY_CELL && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
        return getWinnerStatus(board[0][i], game);
      }
    }

    // Диагонали
    if (board[0][0] != EMPTY_CELL && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
      return getWinnerStatus(board[0][0], game);
    }
    if (board[0][2] != EMPTY_CELL && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
      return getWinnerStatus(board[0][2], game);
    }

    return null;
  }

  private GameStatus getWinnerStatus(int cellValue, Game game) {
    if (game.isVsComputer()) {
      // PvE
      if (cellValue == PLAYER_MARK) return GameStatus.PLAYER_X_WON;
      if (cellValue == COMPUTER_MARK) return GameStatus.COMPUTER_WON;
    } else {
      // PvP
      if (cellValue == PLAYER_X_MARK) return GameStatus.PLAYER_X_WON; // победа игрока 1
      if (cellValue == PLAYER_O_MARK)
        return GameStatus.PLAYER_O_WON; // победа игрока 2 (можно уточнить)
    }
    return GameStatus.IN_PROGRESS;
  }
}
