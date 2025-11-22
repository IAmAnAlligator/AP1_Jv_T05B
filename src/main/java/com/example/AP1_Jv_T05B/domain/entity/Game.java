package com.example.AP1_Jv_T05B.domain.entity;

import com.example.AP1_Jv_T05B.domain.service.GameStatus;
import com.example.AP1_Jv_T05B.domain.util.GameConstants;
import java.time.Instant;
import java.util.UUID;

public class Game {

  private UUID id;
  private GameFieldInterface field;
  private boolean isPlayerTurn; // true - ход игрока, false - ход компьютера
  private GameStatus status;
  private boolean vsComputer;

  private UUID playerXId;
  private UUID playerOId;

  private char playerXMark = 'X';
  private char playerOMark = 'O';

  private UUID currentPlayerId;

  private final Instant createdAt;

  public Game(UUID id, GameFieldInterface field, Instant createdAt) {
    this.id = id;
    this.field = field;
    this.isPlayerTurn = true;
    this.status = GameStatus.WAITING;
    this.createdAt = createdAt;
  }

  public Game() {
    this(UUID.randomUUID(), new GameField(GameConstants.BOARD_SIZE), Instant.now());
  }

  public boolean isFinished() {
    return status == GameStatus.DRAW
        || status == GameStatus.COMPUTER_WON
        || status == GameStatus.PLAYER_X_WON
        || status == GameStatus.PLAYER_O_WON;
  }

  public char getPlayerXMark() {
    return playerXMark;
  }

  public void setPlayerXMark(char playerXMark) {
    this.playerXMark = playerXMark;
  }

  public char getPlayerOMark() {
    return playerOMark;
  }

  public void setPlayerOMark(char playerOMark) {
    this.playerOMark = playerOMark;
  }

  public UUID getPlayerXId() {
    return playerXId;
  }

  public void setPlayerXId(UUID playerXId) {
    this.playerXId = playerXId;
  }

  public UUID getPlayerOId() {
    return playerOId;
  }

  public void setPlayerOId(UUID playerOId) {
    this.playerOId = playerOId;
  }

  public boolean isVsComputer() {
    return vsComputer;
  }

  public void setVsComputer(boolean vsComputer) {
    this.vsComputer = vsComputer;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public GameFieldInterface getGameField() {
    return field;
  }

  public void setGameField(GameFieldInterface field) {
    this.field = field;
  }

  public boolean isPlayerTurn() {
    return isPlayerTurn;
  }

  public void setPlayerTurn(boolean playerTurn) {
    isPlayerTurn = playerTurn;
  }

  public GameStatus getStatus() {
    return status;
  }

  public void setStatus(GameStatus status) {
    this.status = status;
  }

  public UUID getCurrentPlayerId() {
    return currentPlayerId;
  }

  public void setCurrentPlayerId(UUID currentPlayerId) {
    this.currentPlayerId = currentPlayerId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
