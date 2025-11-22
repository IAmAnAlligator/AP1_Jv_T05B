package com.example.AP1_Jv_T05B.web.dto;

import com.example.AP1_Jv_T05B.web.validation.ValidGameField;
import jakarta.validation.constraints.NotNull;

public record UpdateGameRequest(
    @NotNull(message = "Updated field must not be null") @ValidGameField int[][] updatedField) {}
