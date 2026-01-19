package com.example.AP1_Jv_T05B.domain.service;

import static com.example.AP1_Jv_T05B.domain.util.GameConstants.COMPUTER_MARK;
import static com.example.AP1_Jv_T05B.domain.util.GameConstants.EMPTY_CELL;
import static com.example.AP1_Jv_T05B.domain.util.GameConstants.PLAYER_MARK;

import com.example.AP1_Jv_T05B.domain.entity.Game;
import java.awt.Point;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PvEStrategy implements TurnStrategy {

  private final GameLogicService logic;

  public PvEStrategy(GameLogicService logic) {
    this.logic = logic;
  }

  @Override
  public Game processTurn(Game game, UUID currentPlayerId) {

    UUID expectedId = game.isPlayerTurn() ? game.getPlayerXId() : game.getPlayerOId();

    if (!expectedId.equals(currentPlayerId)) {
      throw new IllegalStateException("It's not your turn!");
    }

    game.setPlayerTurn(!game.isPlayerTurn());

    var field = game.getGameField();
    Point move = logic.getNextMove(game);

    if (move != null && field.getCell(move.y, move.x) == EMPTY_CELL) {
      field.setCell(move.y, move.x, COMPUTER_MARK); // теперь COMPUTER_MARK = -1
    }

    GameStatus after = logic.isGameOver(game);
    game.setStatus(after);

    if (after == GameStatus.IN_PROGRESS) {
      game.setPlayerTurn(!game.isPlayerTurn());
      game.setStatus(GameStatus.PLAYER_TURN);
    }

    return game;
  }

  @Override
  public boolean validateGameField(Game currentGame, int[][] updated) {
    int[][] original = currentGame.getGameField().getField();

    if (original.length != updated.length || original[0].length != updated[0].length) {
      return false;
    }

    int changes = 0;
    int currentPlayerValue = currentGame.isPlayerTurn() ? PLAYER_MARK : COMPUTER_MARK;

    for (int y = 0; y < original.length; y++) {
      for (int x = 0; x < original[y].length; x++) {
        if (original[y][x] != EMPTY_CELL && updated[y][x] != original[y][x]) {
          return false; // нельзя менять занятые клетки
        }
        if (original[y][x] == EMPTY_CELL && updated[y][x] != 0) {
          if (updated[y][x] != currentPlayerValue) {
            return false; // игрок ставит не своё значение
          }
          changes++;
        }
      }
    }

    return changes == 1;
  }
}
