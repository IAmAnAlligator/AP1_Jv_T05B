package com.example.AP1_Jv_T05B.exception;

public class CannotJoinException extends RuntimeException {

  public CannotJoinException() {
    super("Cannot join to this game, player ids are equal");
  }
}
