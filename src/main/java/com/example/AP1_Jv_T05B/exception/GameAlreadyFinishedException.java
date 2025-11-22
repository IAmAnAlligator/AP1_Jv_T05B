package com.example.AP1_Jv_T05B.exception;

public class GameAlreadyFinishedException extends RuntimeException {

  public GameAlreadyFinishedException() {
    super("The game is over");
  }
}
