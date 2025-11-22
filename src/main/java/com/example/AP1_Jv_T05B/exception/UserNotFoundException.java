package com.example.AP1_Jv_T05B.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(UUID id) {
    super("User with id " + id + " is not found");
  }
}
