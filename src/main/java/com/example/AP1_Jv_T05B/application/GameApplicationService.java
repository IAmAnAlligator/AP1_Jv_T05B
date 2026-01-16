package com.example.AP1_Jv_T05B.application;

import static com.example.AP1_Jv_T05B.domain.util.GameConstants.COMPUTER_ID;

import com.example.AP1_Jv_T05B.datasource.entity.GameEntity;
import com.example.AP1_Jv_T05B.datasource.mapper.GameMapper;
import com.example.AP1_Jv_T05B.datasource.repository.GameRepository;
import com.example.AP1_Jv_T05B.domain.entity.Game;
import com.example.AP1_Jv_T05B.domain.facade.GameFacade;
import com.example.AP1_Jv_T05B.domain.service.GameStatus;
import com.example.AP1_Jv_T05B.exception.CannotJoinException;
import com.example.AP1_Jv_T05B.exception.DataQueryException;
import com.example.AP1_Jv_T05B.exception.GameAlreadyFinishedException;
import com.example.AP1_Jv_T05B.exception.GameAlreadyStartedException;
import com.example.AP1_Jv_T05B.exception.GameNotFoundException;
import com.example.AP1_Jv_T05B.exception.InvalidGameFieldException;
import com.example.AP1_Jv_T05B.web.dto.LeaderboardUserResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameApplicationService {

  private final GameRepository repository;
  private final GameFacade domain;
  private final UserService userService;

  public GameApplicationService(
      GameRepository repository, GameFacade domain, UserService userService) {
    this.repository = repository;
    this.domain = domain;
    this.userService = userService;
  }

  public boolean checkGameField(Game currentGame, int[][] updated) {
    return domain.validateGameField(currentGame, updated);
  }

  @Transactional
  public Game updateGame(UUID gameId, UUID currentPlayerId, int[][] updatedField) {

    Game game = getGame(gameId);

    if (game.isFinished()) {
      throw new GameAlreadyFinishedException();
    }

    if (!checkGameField(game, updatedField)) {
      throw new InvalidGameFieldException();
    }

    game.getGameField().setField(updatedField);
    game.setCurrentPlayerId(currentPlayerId);

    // 1. Загружаем сущность
    GameEntity entity =
        repository.findById(gameId).orElseThrow(() -> new GameNotFoundException(gameId));

    // 2. Обновляем её из доменной модели
    GameMapper.updateEntity(entity, game);

    // 3. Преобразуем в доменную модель
    Game domainGame = GameMapper.toDomain(entity);

    Game updatedGame = domain.processTurn(domainGame, domainGame.getCurrentPlayerId());

    // 5. Обновляем сущность обратно и сохраняем
    GameMapper.updateEntity(entity, updatedGame);
    repository.save(entity);

    // 6. Возвращаем доменную модель, а не сущность
    return updatedGame;
  }

  @Transactional
  public Game createGame(UUID playerId, boolean vsComputer) {

    userService.getUserById(playerId);

    Game game = new Game();
    game.setPlayerXId(playerId);
    game.setPlayerOId(COMPUTER_ID);

    game.setCurrentPlayerId(playerId);

    game.setVsComputer(vsComputer);
    game.setPlayerXMark('X');
    game.setPlayerOMark('O');

    if (vsComputer) {
      game.setStatus(GameStatus.PLAYER_TURN);
    } else {
      game.setStatus(GameStatus.WAITING);
    }

    GameEntity entity = GameMapper.toEntity(game);
    GameEntity saved = repository.save(entity);
    return GameMapper.toDomain(saved);
  }

  @Transactional(readOnly = true)
  public Game getGame(UUID id) {
    return repository
        .findById(id)
        .map(GameMapper::toDomain)
        .orElseThrow(() -> new GameNotFoundException(id));
  }

  @Transactional(readOnly = true)
  public List<Game> getAllGames() {
    List<GameStatus> statuses =
        List.of(
            GameStatus.WAITING,
            GameStatus.PLAYER_TURN,
            GameStatus.IN_PROGRESS,
            GameStatus.PLAYER_X_TURN,
            GameStatus.PLAYER_O_TURN);
    return repository
        .findAllByStatusIn(
            statuses,
            Sort.by(Sort.Direction.DESC, "createdAt") // новые игры сверху
        )
        .stream()
        .map(GameMapper::toDomain)
        .toList();
  }

  @Transactional
  public Game joinGame(UUID gameId, UUID playerId) {

    if (gameId == null) {
      throw new GameNotFoundException(null);
    }

    Game game = getGame(gameId);

    if (game.getPlayerXId().equals(playerId)) {
      throw new CannotJoinException();
    }

    GameEntity entity =
        repository
            .findById(game.getId())
            .orElseThrow(() -> new GameNotFoundException(game.getId()));

    if (entity.getStatus() != GameStatus.WAITING) {
      throw new GameAlreadyStartedException();
    }

    entity.setPlayerOId(playerId);
    entity.setStatus(GameStatus.PLAYER_X_TURN);
    entity.setPlayerTurn(true);

    GameEntity saved = repository.save(entity);
    return GameMapper.toDomain(saved);
  }

  public List<Game> getFinishedGames(UUID userId) {
    try {
      return repository.findFinishedGamesByUser(userId).stream().map(GameMapper::toDomain).toList();
    } catch (DataAccessException ex) {
      throw new DataQueryException("Cannot load a table of finished game", ex);
    }
  }

  public List<LeaderboardUserResponse> getTopPlayers(int limit) {
    try {
      return repository.findLeaderboard(limit).stream()
          .map(
              row ->
                  new LeaderboardUserResponse(
                      (UUID) row[0], // user_id
                      (String) row[1], // login
                      ((Number) row[5]).doubleValue() // ratio
                      ))
          .toList();
    } catch (DataAccessException ex) {
      throw new DataQueryException("Cannot load a table of leaders", ex);
    }
  }
}
