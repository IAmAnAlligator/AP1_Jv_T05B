package com.example.AP1_Jv_T05B.web.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record JoinGameRequest(@NotNull(message = "GameId is required") UUID gameId) {}
