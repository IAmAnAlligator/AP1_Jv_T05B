package com.example.AP1_Jv_T05B.domain.service;

import static com.example.AP1_Jv_T05B.domain.util.GameConstants.EMPTY_CELL;
import static com.example.AP1_Jv_T05B.domain.util.GameConstants.PLAYER_O_MARK;
import static com.example.AP1_Jv_T05B.domain.util.GameConstants.PLAYER_X_MARK;

import com.example.AP1_Jv_T05B.domain.entity.Game;
import com.example.AP1_Jv_T05B.exception.NotYourTurnException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PvPStrategy implements TurnStrategy {

  private final GameLogicService logic;

  public PvPStrategy(GameLogicService logic) {
    this.logic = logic;
  }

  @Override
  public Game processTurn(Game game, UUID currentPlayerId) {

    // 1. Определяем, кто должен ходить
    UUID expectedId = game.isPlayerTurn() ? game.getPlayerXId() : game.getPlayerOId();

    // 2. Проверяем, тот ли игрок делает ход
    if (!expectedId.equals(currentPlayerId)) {
      throw new NotYourTurnException();
    }

    // 3. Проверяем, закончилась ли игра после сделанного хода
    GameStatus after = logic.isGameOver(game);

    if (after == GameStatus.PLAYER_X_WON) {
      game.setStatus(GameStatus.PLAYER_X_WON);
      return game;
    } else if (after == GameStatus.PLAYER_O_WON) {
      game.setStatus(GameStatus.PLAYER_O_WON);
      return game;
    } else if (after == GameStatus.DRAW) {
      game.setStatus(GameStatus.DRAW);
      return game;
    }

    // 4. Игра продолжается → меняем ход
    game.setPlayerTurn(!game.isPlayerTurn());

    // 5. Присваиваем статус соответствующего игрока
    if (game.isPlayerTurn()) {
      game.setStatus(GameStatus.PLAYER_X_TURN);
    } else {
      game.setStatus(GameStatus.PLAYER_O_TURN);
    }

    return game;
  }

  @Override
  public boolean validateGameField(Game currentGame, int[][] updated) {
    int[][] original = currentGame.getGameField().getField();

    if (original.length != updated.length || original[0].length != updated[0].length) {
      return false;
    }

    // Определяем, чей ход (1 или 2)
    int count1 = 0, count2 = 0;
    for (int[] row : original) {
      for (int cell : row) {
        if (cell == 1) {
          count1++;
        } else if (cell == -1) {
          count2++;
        }
      }
    }
    int currentPlayerValue = (count1 == count2) ? PLAYER_X_MARK : PLAYER_O_MARK;

    int diffCount = 0;

    for (int y = 0; y < original.length; y++) {
      for (int x = 0; x < original[y].length; x++) {
        int oldCell = original[y][x];
        int newCell = updated[y][x];

        if (oldCell == EMPTY_CELL && newCell == currentPlayerValue) {
          diffCount++;
        } else if (oldCell != newCell) {
          // запрещено изменять уже занятые клетки или ставить чужой символ
          return false;
        }
      }
    }

    return diffCount == 1;
  }
}
