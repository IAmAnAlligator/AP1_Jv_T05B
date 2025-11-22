package com.example.AP1_Jv_T05B.web.dto;

import com.example.AP1_Jv_T05B.domain.service.GameStatus;
import java.util.UUID;

public record GameResponse(
    UUID id,
    int[][] field,
    GameStatus status,
    UUID playerXId,
    UUID playerOId,
    char playerXMark,
    char playerOMark,
    boolean vsComputer,
    String createdAt) {}
