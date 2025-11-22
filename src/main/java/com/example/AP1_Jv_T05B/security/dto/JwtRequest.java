package com.example.AP1_Jv_T05B.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JwtRequest(
    @NotNull
        @NotBlank(message = "Username is mandatory")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        String login,
    @NotNull @NotBlank @Size(min = 3, message = "Password must be at least 3 characters")
        String password) {}
