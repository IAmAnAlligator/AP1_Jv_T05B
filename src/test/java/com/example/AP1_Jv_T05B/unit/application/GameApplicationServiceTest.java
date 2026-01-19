package com.example.AP1_Jv_T05B.unit.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.AP1_Jv_T05B.application.GameApplicationService;
import com.example.AP1_Jv_T05B.application.UserService;
import com.example.AP1_Jv_T05B.datasource.entity.GameEntity;
import com.example.AP1_Jv_T05B.datasource.mapper.GameMapper;
import com.example.AP1_Jv_T05B.datasource.repository.GameRepository;
import com.example.AP1_Jv_T05B.domain.entity.Game;
import com.example.AP1_Jv_T05B.domain.facade.GameFacade;
import com.example.AP1_Jv_T05B.domain.service.GameStatus;
import com.example.AP1_Jv_T05B.exception.*;
import com.example.AP1_Jv_T05B.web.dto.LeaderboardUserResponse;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

@ExtendWith(MockitoExtension.class)
class GameApplicationServiceTest {

  @Mock private GameRepository repository;
  @Mock private GameFacade domain;
  @Mock private UserService userService;

  private GameApplicationService service;

  private UUID gameId;
  private UUID playerX;
  private UUID playerO;

  @BeforeEach
  void setUp() {
    service = new GameApplicationService(repository, domain, userService);
    gameId = UUID.randomUUID();
    playerX = UUID.randomUUID();
    playerO = UUID.randomUUID();
  }

  // ---------- checkGameField ----------

  @Test
  void checkGameField_DelegatesToDomain() {
    Game game = new Game();
    int[][] field = new int[3][3];

    when(domain.validateGameField(game, field)).thenReturn(true);

    assertTrue(service.checkGameField(game, field));
  }

  // ---------- createGame ----------

  @Test
  void createGame_VsComputer_SetsCorrectStatus() {
    when(userService.getUserById(playerX)).thenReturn(null);

    GameEntity saved = new GameEntity();
    saved.setPlayerXId(playerX);
    saved.setStatus(GameStatus.PLAYER_TURN);

    when(repository.save(any())).thenReturn(saved);

    try (MockedStatic<GameMapper> mapper = mockStatic(GameMapper.class)) {
      mapper.when(() -> GameMapper.toEntity(any())).thenReturn(new GameEntity());
      mapper.when(() -> GameMapper.toDomain(saved)).thenReturn(new Game());

      Game game = service.createGame(playerX, true);

      assertNotNull(game);
    }
  }

  // ---------- getGame ----------

  @Test
  void getGame_Found_ReturnsGame() {
    GameEntity entity = new GameEntity();

    when(repository.findById(gameId)).thenReturn(Optional.of(entity));

    try (MockedStatic<GameMapper> mapper = mockStatic(GameMapper.class)) {
      mapper.when(() -> GameMapper.toDomain(entity)).thenReturn(new Game());

      assertNotNull(service.getGame(gameId));
    }
  }

  @Test
  void getGame_NotFound_ThrowsException() {
    when(repository.findById(gameId)).thenReturn(Optional.empty());

    assertThrows(GameNotFoundException.class, () -> service.getGame(gameId));
  }

  // ---------- joinGame ----------

  @Test
  void joinGame_Success() {
    GameEntity entity = new GameEntity();
    entity.setStatus(GameStatus.WAITING);

    when(repository.findById(gameId)).thenReturn(Optional.of(entity));
    when(repository.save(entity)).thenReturn(entity);

    try (MockedStatic<GameMapper> mapper = mockStatic(GameMapper.class)) {

      mapper
          .when(() -> GameMapper.toDomain(any(GameEntity.class)))
          .thenAnswer(
              invocation -> {
                GameEntity e = invocation.getArgument(0);

                Game g = new Game();
                g.setId(gameId);
                g.setPlayerXId(playerX);
                g.setPlayerOId(playerO);
                g.setStatus(e.getStatus());
                g.setPlayerTurn(true);

                return g;
              });

      Game result = service.joinGame(gameId, playerO);

      assertEquals(GameStatus.PLAYER_X_TURN, result.getStatus());
      assertEquals(playerO, result.getPlayerOId());
      assertTrue(result.isPlayerTurn());
    }
  }

  @Test
  void joinGame_GameAlreadyStarted_ThrowsException() {
    GameEntity entity = new GameEntity();
    entity.setStatus(GameStatus.PLAYER_X_TURN);

    when(repository.findById(gameId)).thenReturn(Optional.of(entity));

    try (MockedStatic<GameMapper> mapper = mockStatic(GameMapper.class)) {

      Game game = new Game();
      game.setId(gameId);
      game.setStatus(GameStatus.PLAYER_X_TURN);
      game.setPlayerXId(playerX);
      game.setPlayerOId(null);

      mapper.when(() -> GameMapper.toDomain(any(GameEntity.class))).thenReturn(game);

      assertThrows(GameAlreadyStartedException.class, () -> service.joinGame(gameId, playerO));
    }
  }

  // ---------- updateGame ----------

  @Test
  void updateGame_GameFinished_ThrowsException() {
    Game game = new Game();

    game.setStatus(GameStatus.COMPUTER_WON);

    when(repository.findById(gameId)).thenReturn(Optional.of(new GameEntity()));

    try (MockedStatic<GameMapper> mapper = mockStatic(GameMapper.class)) {
      mapper.when(() -> GameMapper.toDomain(any())).thenReturn(game);

      assertThrows(
          GameAlreadyFinishedException.class,
          () -> service.updateGame(gameId, playerX, new int[3][3]));
    }
  }

  // ---------- getFinishedGames ----------

  @Test
  void getFinishedGames_DataAccessError_ThrowsWrappedException() {
    when(repository.findFinishedGamesByUser(playerX))
        .thenThrow(new DataAccessException("db error") {});

    assertThrows(DataQueryException.class, () -> service.getFinishedGames(playerX));
  }

  // ---------- getTopPlayers ----------

  @Test
  void getTopPlayers_ReturnsLeaderboard() {
    Object[] row = new Object[] {playerX, "alex", null, null, null, 0.75};
    when(repository.findLeaderboard(5)).thenReturn(List.<Object[]>of(row));

    List<LeaderboardUserResponse> result = service.getTopPlayers(5);

    assertEquals(1, result.size());
    assertEquals(playerX, result.get(0).userId());
    assertEquals(0.75, result.get(0).ratio());
  }
}
