package com.example.AP1_Jv_T05B.domain.util;

import com.example.AP1_Jv_T05B.domain.entity.GameFieldInterface;

public final class GameFieldPrinter {

  private GameFieldPrinter() {}

  public static void printField(GameFieldInterface field) {
    int[][] board = field.getField();
    System.out.println("Current field:");
    for (int y = 0; y < board.length; y++) {
      for (int x = 0; x < board[y].length; x++) {
        char c = board[y][x] == 1 ? 'X' : board[y][x] == -1 ? 'O' : '.';
        System.out.print(c + " ");
      }
      System.out.println();
    }
    System.out.println("-----");
  }
}
