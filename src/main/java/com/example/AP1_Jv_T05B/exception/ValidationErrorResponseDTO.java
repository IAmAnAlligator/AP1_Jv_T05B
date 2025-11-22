package com.example.AP1_Jv_T05B.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponseDTO(LocalDateTime timestamp, Map<String, String> errors) {}
