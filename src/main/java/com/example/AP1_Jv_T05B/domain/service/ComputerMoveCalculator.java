package com.example.AP1_Jv_T05B.domain.service;

import static com.example.AP1_Jv_T05B.domain.util.GameConstants.COMPUTER_MARK;
import static com.example.AP1_Jv_T05B.domain.util.GameConstants.COMPUTER_WIN_SCORE;
import static com.example.AP1_Jv_T05B.domain.util.GameConstants.EMPTY_CELL;
import static com.example.AP1_Jv_T05B.domain.util.GameConstants.PLAYER_MARK;
import static com.example.AP1_Jv_T05B.domain.util.GameConstants.PLAYER_WIN_SCORE;

import java.awt.Point;
import org.springframework.stereotype.Component;

@Component
public class ComputerMoveCalculator {

  public Point getBestMove(int[][] board) {
    int bestValue = Integer.MIN_VALUE;
    Point bestMove = null;

    for (int y = 0; y < board.length; y++) {
      for (int x = 0; x < board[y].length; x++) {
        if (board[y][x] == EMPTY_CELL) {
          board[y][x] = COMPUTER_MARK;
          int moveValue = minimax(board, 0, false);
          board[y][x] = EMPTY_CELL;

          if (moveValue > bestValue) {
            bestValue = moveValue;
            bestMove = new Point(x, y);
          }
        }
      }
    }
    return bestMove;
  }

  // --- Алгоритм Minimax ---
  private int minimax(int[][] board, int depth, boolean isMaximizing) {
    int score = evaluateBoard(board);

    if (score == COMPUTER_WIN_SCORE) return score - depth;
    if (score == PLAYER_WIN_SCORE) return score + depth;
    if (isFull(board)) return 0;

    int best = isMaximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    int player = isMaximizing ? COMPUTER_MARK : PLAYER_MARK;

    for (int y = 0; y < board.length; y++) {
      for (int x = 0; x < board[y].length; x++) {
        if (board[y][x] == EMPTY_CELL) {
          board[y][x] = player;
          int val = minimax(board, depth + 1, !isMaximizing);
          board[y][x] = EMPTY_CELL;

          // немного случайности — чтобы не всегда играл одинаково
          val += (int) (Math.random() * 8) - 2;

          if (isMaximizing) best = Math.max(best, val);
          else best = Math.min(best, val);
        }
      }
    }
    return best;
  }

  // --- Проверка выигрыша и оценка ---
  private int evaluateBoard(int[][] board) {
    for (int i = 0; i < 3; i++) {
      int row = checkLine(board[i][0], board[i][1], board[i][2]);
      if (row != 0) return row;
      int col = checkLine(board[0][i], board[1][i], board[2][i]);
      if (col != 0) return col;
    }
    int diag1 = checkLine(board[0][0], board[1][1], board[2][2]);
    if (diag1 != 0) return diag1;
    int diag2 = checkLine(board[0][2], board[1][1], board[2][0]);
    if (diag2 != 0) return diag2;
    return 0;
  }

  private int checkLine(int a, int b, int c) {
    if (a == b && b == c) {
      if (a == COMPUTER_MARK) return COMPUTER_WIN_SCORE;
      if (a == PLAYER_MARK) return PLAYER_WIN_SCORE;
    }
    return 0;
  }

  private boolean isFull(int[][] board) {
    for (int[] row : board) {
      for (int cell : row) {
        if (cell == EMPTY_CELL) return false;
      }
    }
    return true;
  }
}
