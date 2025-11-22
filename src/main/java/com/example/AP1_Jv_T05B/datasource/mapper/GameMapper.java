package com.example.AP1_Jv_T05B.datasource.mapper;

import com.example.AP1_Jv_T05B.datasource.entity.GameEntity;
import com.example.AP1_Jv_T05B.domain.entity.Game;
import com.example.AP1_Jv_T05B.domain.entity.GameField;
import com.example.AP1_Jv_T05B.domain.service.GameStatus;

public class GameMapper {

  public static void updateEntity(GameEntity entity, Game game) {
    entity.setField(game.getGameField().getField());
    entity.setPlayerTurn(game.isPlayerTurn());
    entity.setStatus(game.getStatus());
    entity.setCurrentPlayerId(game.getCurrentPlayerId());
  }

  public static GameEntity toEntity(Game game) {
    return new GameEntity(
        game.getId(),
        game.getGameField().getField(),
        game.isPlayerTurn(),
        game.getStatus(),
        game.getPlayerXId(),
        game.getPlayerOId(),
        game.getPlayerXMark(),
        game.getPlayerOMark(),
        game.getCurrentPlayerId(),
        game.isVsComputer(),
        game.getCreatedAt());
  }

  public static Game toDomain(GameEntity entity) {
    Game game = new Game(entity.getId(), new GameField(entity.getField()), entity.getCreatedAt());

    game.setStatus(entity.getStatus() != null ? entity.getStatus() : GameStatus.WAITING);
    game.setVsComputer(entity.isVsComputer());
    game.setPlayerXId(entity.getPlayerXId());
    game.setPlayerOId(entity.getPlayerOId());
    game.setPlayerXMark(entity.getPlayerXSymbol());
    game.setPlayerOMark(entity.getPlayerOSymbol());
    game.setCurrentPlayerId(entity.getCurrentPlayerId());
    game.setPlayerTurn(entity.isPlayerTurn());

    return game;
  }
}
