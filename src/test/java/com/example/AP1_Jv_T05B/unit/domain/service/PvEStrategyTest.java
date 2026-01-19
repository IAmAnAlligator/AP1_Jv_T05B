package com.example.AP1_Jv_T05B.unit.domain.service;

import static com.example.AP1_Jv_T05B.domain.util.GameConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.AP1_Jv_T05B.domain.entity.Game;
import com.example.AP1_Jv_T05B.domain.entity.GameFieldInterface;
import com.example.AP1_Jv_T05B.domain.service.GameLogicService;
import com.example.AP1_Jv_T05B.domain.service.GameStatus;
import com.example.AP1_Jv_T05B.domain.service.PvEStrategy;
import java.awt.Point;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PvEStrategyTest {

  @Mock private GameLogicService logic;

  @Mock private Game game;

  @Mock private GameFieldInterface field;

  private PvEStrategy strategy;

  private UUID playerId;

  @BeforeEach
  void setUp() {
    strategy = new PvEStrategy(logic);
    playerId = UUID.randomUUID();
  }

  // ---------- processTurn ----------

  @Test
  void throwsExceptionWhenNotPlayersTurn() {
    UUID realPlayerId = UUID.randomUUID();
    UUID otherPlayerId = UUID.randomUUID();

    when(game.isPlayerTurn()).thenReturn(true);
    when(game.getPlayerXId()).thenReturn(realPlayerId);

    assertThrows(IllegalStateException.class, () -> strategy.processTurn(game, otherPlayerId));
  }

  @Test
  void computerMakesMoveAndGameContinues() {
    when(game.getGameField()).thenReturn(field);
    when(game.getPlayerXId()).thenReturn(playerId);
    when(game.isPlayerTurn()).thenReturn(true);
    when(field.getCell(1, 1)).thenReturn(EMPTY_CELL);
    when(logic.getNextMove(game)).thenReturn(new Point(1, 1));
    when(logic.isGameOver(game)).thenReturn(GameStatus.IN_PROGRESS);

    Game result = strategy.processTurn(game, playerId);

    verify(field).setCell(1, 1, COMPUTER_MARK);
    verify(game).setStatus(GameStatus.PLAYER_TURN);
    verify(game, times(2)).setPlayerTurn(anyBoolean());

    assertSame(game, result);
  }

  @Test
  void computerWinsGame() {
    when(game.getGameField()).thenReturn(field);
    when(game.getPlayerXId()).thenReturn(playerId);
    when(game.isPlayerTurn()).thenReturn(true);
    when(field.getCell(0, 0)).thenReturn(EMPTY_CELL);
    when(logic.getNextMove(game)).thenReturn(new Point(0, 0));
    when(logic.isGameOver(game)).thenReturn(GameStatus.COMPUTER_WON);

    strategy.processTurn(game, playerId);

    verify(field).setCell(0, 0, COMPUTER_MARK);
    verify(game).setStatus(GameStatus.COMPUTER_WON);
    verify(game).setPlayerTurn(anyBoolean());
  }

  // ---------- validateGameField ----------

  @Test
  void validSinglePlayerMove() {
    int[][] original = {
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
    };

    int[][] updated = {
      {1, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
    };

    when(game.getGameField()).thenReturn(field);
    when(game.isPlayerTurn()).thenReturn(true);
    when(field.getField()).thenReturn(original);

    assertTrue(strategy.validateGameField(game, updated));
  }

  @Test
  void invalid_WhenChangingOccupiedCell() {
    int[][] original = {
      {1, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
    };

    int[][] updated = {
      {COMPUTER_MARK, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
    };

    when(game.getGameField()).thenReturn(field);
    when(game.isPlayerTurn()).thenReturn(true);
    when(field.getField()).thenReturn(original);

    assertFalse(strategy.validateGameField(game, updated));
  }

  @Test
  void invalid_WhenMoreThanOneMove() {
    int[][] original = {
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
    };

    int[][] updated = {
      {1, 1, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
    };

    when(game.getGameField()).thenReturn(field);
    when(game.isPlayerTurn()).thenReturn(true);
    when(field.getField()).thenReturn(original);

    assertFalse(strategy.validateGameField(game, updated));
  }

  @Test
  void invalid_WhenPlayerUsesWrongSymbol() {
    int[][] original = {
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
    };

    int[][] updated = {
      {COMPUTER_MARK, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
      {EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
    };

    when(game.getGameField()).thenReturn(field);
    when(game.isPlayerTurn()).thenReturn(true);
    when(field.getField()).thenReturn(original);

    assertFalse(strategy.validateGameField(game, updated));
  }
}
