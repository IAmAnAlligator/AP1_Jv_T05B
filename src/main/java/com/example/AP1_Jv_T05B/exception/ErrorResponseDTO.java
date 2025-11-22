package com.example.AP1_Jv_T05B.exception;

import java.time.LocalDateTime;

public record ErrorResponseDTO(String message, LocalDateTime timestamp) {}
