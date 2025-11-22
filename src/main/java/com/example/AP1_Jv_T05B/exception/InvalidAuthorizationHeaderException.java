package com.example.AP1_Jv_T05B.exception;

public class InvalidAuthorizationHeaderException extends RuntimeException {
  public InvalidAuthorizationHeaderException() {
    super("Invalid or missing Authorization header");
  }
}
