package com.example.AP1_Jv_T05B.unit.domain.service;

import static com.example.AP1_Jv_T05B.domain.util.GameConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.AP1_Jv_T05B.domain.entity.Game;
import com.example.AP1_Jv_T05B.domain.entity.GameFieldInterface;
import com.example.AP1_Jv_T05B.domain.service.GameLogicService;
import com.example.AP1_Jv_T05B.domain.service.GameStatus;
import com.example.AP1_Jv_T05B.domain.service.PvPStrategy;
import com.example.AP1_Jv_T05B.exception.NotYourTurnException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PvPStrategyTest {

  @Mock private GameLogicService logic;

  @Mock private Game game;

  @Mock private GameFieldInterface field;

  private PvPStrategy strategy;

  private UUID playerXId;
  private UUID playerOId;

  @BeforeEach
  void setUp() {
    strategy = new PvPStrategy(logic);
    playerXId = UUID.randomUUID();
    playerOId = UUID.randomUUID();
  }

  // ----------------- processTurn -----------------

  @Test
  void throwsIfNotPlayersTurn() {

    UUID playerX = UUID.randomUUID();
    UUID wrongPlayer = UUID.randomUUID();

    // Игрок X ходит
    when(game.isPlayerTurn()).thenReturn(true);
    when(game.getPlayerXId()).thenReturn(playerX);

    assertThrows(NotYourTurnException.class, () -> strategy.processTurn(game, wrongPlayer));
  }

  @Test
  void playerXWins_GameStatusUpdated() {
    when(game.getPlayerXId()).thenReturn(playerXId);
    when(game.isPlayerTurn()).thenReturn(true);
    when(logic.isGameOver(game)).thenReturn(GameStatus.PLAYER_X_WON);

    strategy.processTurn(game, playerXId);

    verify(game).setStatus(GameStatus.PLAYER_X_WON);
  }

  @Test
  void playerOTurn_GameContinues_StatusAndTurnUpdated() {
    when(game.getPlayerOId()).thenReturn(playerOId);
    when(game.isPlayerTurn()).thenReturn(false, false, true);
    when(logic.isGameOver(game)).thenReturn(GameStatus.IN_PROGRESS);

    strategy.processTurn(game, playerOId);

    verify(game).setPlayerTurn(true);
    verify(game).setStatus(GameStatus.PLAYER_X_TURN);
  }

  // ----------------- validateGameField -----------------

  @Test
  void validMoveForCurrentPlayer() {
    int[][] original = {
      {0, 0, 0},
      {0, 0, 0},
      {0, 0, 0}
    };
    int[][] updated = {
      {PLAYER_X_MARK, 0, 0},
      {0, 0, 0},
      {0, 0, 0}
    };

    when(field.getField()).thenReturn(original);
    when(game.getGameField()).thenReturn(field);

    assertTrue(strategy.validateGameField(game, updated));
  }

  @Test
  void invalid_WhenOccupiedCellChanged() {
    int[][] original = {
      {PLAYER_X_MARK, 0, 0},
      {0, 0, 0},
      {0, 0, 0}
    };
    int[][] updated = {
      {PLAYER_O_MARK, 0, 0}, // изменена занятая клетка
      {0, 0, 0},
      {0, 0, 0}
    };

    when(game.getGameField()).thenReturn(field);
    when(field.getField()).thenReturn(original);

    assertFalse(strategy.validateGameField(game, updated));
  }

  @Test
  void invalid_WhenWrongPlayerSymbol() {
    int[][] original = {
      {0, 0, 0},
      {0, 0, 0},
      {0, 0, 0}
    };
    int[][] updated = {
      {PLAYER_O_MARK, 0, 0}, // ход PLAYER_O_MARK не соответствует текущему игроку
      {0, 0, 0},
      {0, 0, 0}
    };

    when(game.getGameField()).thenReturn(field);
    when(field.getField()).thenReturn(original);

    assertFalse(strategy.validateGameField(game, updated));
  }

  @Test
  void invalid_WhenMoreThanOneCellChanged() {
    int[][] original = {
      {0, 0, 0},
      {0, 0, 0},
      {0, 0, 0}
    };
    int[][] updated = {
      {PLAYER_X_MARK, PLAYER_X_MARK, 0}, // две клетки изменены
      {0, 0, 0},
      {0, 0, 0}
    };

    when(game.getGameField()).thenReturn(field);
    when(field.getField()).thenReturn(original);

    assertFalse(strategy.validateGameField(game, updated));
  }

  @Test
  void invalid_WhenSizeMismatch() {
    int[][] original = {
      {0, 0, 0},
      {0, 0, 0},
      {0, 0, 0}
    };
    int[][] updated = {
      {0, 0},
      {0, 0}
    };

    when(game.getGameField()).thenReturn(field);
    when(field.getField()).thenReturn(original);

    assertFalse(strategy.validateGameField(game, updated));
  }
}
