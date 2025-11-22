package com.example.AP1_Jv_T05B.exception;

public class GameAlreadyStartedException extends RuntimeException {

  public GameAlreadyStartedException() {
    super("Cannot join: the game is already started or finished");
  }
}
