package com.example.AP1_Jv_T05B.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RefreshJwtRequest(
    @NotNull @NotBlank(message = "Refresh token is blank") String refreshToken) {}
