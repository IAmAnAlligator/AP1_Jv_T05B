package com.example.AP1_Jv_T05B.web.controller;

import com.example.AP1_Jv_T05B.application.GameApplicationService;
import com.example.AP1_Jv_T05B.domain.entity.Game;
import com.example.AP1_Jv_T05B.web.dto.CreateGameRequest;
import com.example.AP1_Jv_T05B.web.dto.GameResponse;
import com.example.AP1_Jv_T05B.web.dto.JoinGameRequest;
import com.example.AP1_Jv_T05B.web.dto.LeaderboardUserResponse;
import com.example.AP1_Jv_T05B.web.dto.UpdateGameRequest;
import com.example.AP1_Jv_T05B.web.mapper.GameMapper;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
public class GameController {

  private final GameApplicationService gameService;

  public GameController(GameApplicationService gameService) {
    this.gameService = gameService;
  }

  @PostMapping
  public ResponseEntity<GameResponse> createGame(
      @AuthenticationPrincipal UUID userId, @RequestBody CreateGameRequest request) {

    Game createdGame = gameService.createGame(userId, request.vsComputer());
    URI location = URI.create("/games/" + createdGame.getId());

    return ResponseEntity.created(location).body(GameMapper.toDTO(createdGame));
  }

  @PostMapping("/join")
  public ResponseEntity<GameResponse> joinGame(
      @AuthenticationPrincipal UUID userId, @RequestBody @Valid JoinGameRequest request) {

    Game updated = gameService.joinGame(request.gameId(), userId);
    return ResponseEntity.ok(GameMapper.toDTO(updated));
  }

  @PutMapping("/{gameId}")
  public ResponseEntity<GameResponse> updateGame(
      @AuthenticationPrincipal UUID userId,
      @PathVariable UUID gameId,
      @RequestBody @Valid UpdateGameRequest request) {

    Game updatedGame = gameService.updateGame(gameId, userId, request.updatedField());
    return ResponseEntity.ok(GameMapper.toDTO(updatedGame));
  }

  @GetMapping("/available")
  public ResponseEntity<List<GameResponse>> getAvailableGames() {
    List<GameResponse> result = gameService.getAllGames().stream().map(GameMapper::toDTO).toList();
    return ResponseEntity.ok(result);
  }

  @GetMapping("/{gameId}")
  public ResponseEntity<GameResponse> getGameById(@PathVariable UUID gameId) {
    Game game = gameService.getGame(gameId);
    return ResponseEntity.ok(GameMapper.toDTO(game));
  }

  @GetMapping("/finished")
  public ResponseEntity<List<GameResponse>> getFinishedGames(@AuthenticationPrincipal UUID userId) {
    return ResponseEntity.ok(
        gameService.getFinishedGames(userId).stream().map(GameMapper::toDTO).toList());
  }

  @GetMapping("/leaderboard")
  public List<LeaderboardUserResponse> getTopPlayers(@RequestParam(defaultValue = "10") int limit) {
    return gameService.getTopPlayers(limit);
  }
}
