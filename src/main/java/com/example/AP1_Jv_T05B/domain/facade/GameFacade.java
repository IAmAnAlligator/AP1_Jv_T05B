package com.example.AP1_Jv_T05B.domain.facade;

import com.example.AP1_Jv_T05B.domain.entity.Game;
import com.example.AP1_Jv_T05B.domain.service.GameLogic;
import com.example.AP1_Jv_T05B.domain.service.GameLogicService;
import com.example.AP1_Jv_T05B.domain.service.GameStatus;
import com.example.AP1_Jv_T05B.domain.service.PvEStrategy;
import com.example.AP1_Jv_T05B.domain.service.PvPStrategy;
import com.example.AP1_Jv_T05B.domain.service.TurnStrategy;
import java.awt.Point;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GameFacade implements GameLogic, TurnStrategy {

  private final PvEStrategy pve;
  private final PvPStrategy pvp;
  private final GameLogicService logic;

  public GameFacade(GameLogicService logic, PvEStrategy pve, PvPStrategy pvp) {
    this.logic = logic;
    this.pve = pve;
    this.pvp = pvp;
  }

  @Override
  public Game processTurn(Game game, UUID currentPlayerId) {
    if (game.isVsComputer()) {
      return pve.processTurn(game, currentPlayerId);
    } else {
      return pvp.processTurn(game, currentPlayerId);
    }
  }

  @Override
  public Point getNextMove(Game game) {
    return logic.getNextMove(game);
  }

  @Override
  public boolean validateGameField(Game currentGame, int[][] updatedField) {
    if (currentGame.isVsComputer()) {
      return pve.validateGameField(currentGame, updatedField);
    } else {
      return pvp.validateGameField(currentGame, updatedField);
    }
  }

  @Override
  public GameStatus isGameOver(Game game) {
    return logic.isGameOver(game);
  }
}
