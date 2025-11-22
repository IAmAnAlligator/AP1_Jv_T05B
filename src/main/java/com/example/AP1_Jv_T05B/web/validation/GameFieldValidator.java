package com.example.AP1_Jv_T05B.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GameFieldValidator implements ConstraintValidator<ValidGameField, int[][]> {

  @Override
  public boolean isValid(int[][] value, ConstraintValidatorContext context) {
    if (value == null) return false;
    if (value.length != 3) return false;
    for (int[] row : value) {
      if (row == null || row.length != 3) return false;
    }
    return true;
  }
}
