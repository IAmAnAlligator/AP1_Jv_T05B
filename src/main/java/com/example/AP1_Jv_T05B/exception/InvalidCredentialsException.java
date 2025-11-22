package com.example.AP1_Jv_T05B.exception;

public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException() {
    super("Invalid login or password");
  }
}
