package com.example.AP1_Jv_T05B.exception;

import java.util.UUID;

public class GameNotFoundException extends RuntimeException {

  public GameNotFoundException(UUID id) {
    super("Game with id " + id + " is not found");
  }
}
