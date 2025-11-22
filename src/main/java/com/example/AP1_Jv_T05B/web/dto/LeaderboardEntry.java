package com.example.AP1_Jv_T05B.web.dto;

import java.util.UUID;

public record LeaderboardEntry(UUID userId, long wins, long losses, long draws, double ratio) {}
