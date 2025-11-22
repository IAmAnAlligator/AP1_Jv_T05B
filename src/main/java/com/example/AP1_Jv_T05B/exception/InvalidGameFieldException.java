package com.example.AP1_Jv_T05B.exception;

public class InvalidGameFieldException extends RuntimeException {

  public InvalidGameFieldException() {
    super("Invalid game board");
  }
}
