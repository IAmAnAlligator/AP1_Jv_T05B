package com.example.AP1_Jv_T05B.web.dto;

import java.util.UUID;

public record LeaderboardUserResponse(UUID userId, String login, double ratio) {}
