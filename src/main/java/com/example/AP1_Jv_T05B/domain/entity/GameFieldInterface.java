package com.example.AP1_Jv_T05B.domain.entity;

public interface GameFieldInterface {

  int[][] getField();

  void setField(int[][] field);

  int getSize();

  int getCell(int row, int col);

  void setCell(int row, int col, int value);

  GameFieldInterface copyField();
}
