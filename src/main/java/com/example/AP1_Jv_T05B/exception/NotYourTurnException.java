package com.example.AP1_Jv_T05B.exception;

public class NotYourTurnException extends RuntimeException {

  public NotYourTurnException() {
    super("It's not your turn!");
  }
}
