package com.example.AP1_Jv_T05B.domain.entity;

public class GameField implements GameFieldInterface {

  private int[][] field;
  private final int fieldSize;

  public GameField(int fieldSize) {
    this.fieldSize = fieldSize;
    this.field = new int[fieldSize][fieldSize];
  }

  public GameField(int[][] field) {
    this.fieldSize = field.length;
    this.field = new int[fieldSize][fieldSize];
    for (int i = 0; i < fieldSize; i++) {
      System.arraycopy(field[i], 0, this.field[i], 0, fieldSize);
    }
  }

  @Override
  public int[][] getField() {
    int[][] copy = new int[fieldSize][fieldSize];
    for (int i = 0; i < fieldSize; i++) {
      System.arraycopy(this.field[i], 0, copy[i], 0, fieldSize);
    }
    return copy;
  }

  @Override
  public void setField(int[][] field) {
    this.field = new int[fieldSize][fieldSize];
    for (int i = 0; i < fieldSize; i++) {
      System.arraycopy(field[i], 0, this.field[i], 0, fieldSize);
    }
  }

  @Override
  public int getSize() {
    return fieldSize;
  }

  @Override
  public int getCell(int row, int col) {
    return field[row][col];
  }

  @Override
  public void setCell(int row, int col, int value) {
    field[row][col] = value;
  }

  @Override
  public GameFieldInterface copyField() {
    return new GameField(this.field);
  }
}
