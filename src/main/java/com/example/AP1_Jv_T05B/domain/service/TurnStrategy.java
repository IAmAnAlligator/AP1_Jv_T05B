package com.example.AP1_Jv_T05B.domain.service;

import com.example.AP1_Jv_T05B.domain.entity.Game;
import java.util.UUID;

public interface TurnStrategy {

  Game processTurn(Game game, UUID currentPlayerId);

  boolean validateGameField(Game currentGame, int[][] updated);
}
