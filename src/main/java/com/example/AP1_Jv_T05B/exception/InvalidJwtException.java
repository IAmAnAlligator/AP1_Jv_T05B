package com.example.AP1_Jv_T05B.exception;

public class InvalidJwtException extends RuntimeException {

  public InvalidJwtException(String message) {
    super(message);
  }
}
