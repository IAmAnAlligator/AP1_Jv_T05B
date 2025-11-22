package com.example.AP1_Jv_T05B.datasource.entity;

import com.example.AP1_Jv_T05B.datasource.converter.IntArrayConverter;
import com.example.AP1_Jv_T05B.domain.service.GameStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "games")
public class GameEntity {

  @Id private UUID id;

  @Convert(converter = IntArrayConverter.class)
  @Column(columnDefinition = "text", nullable = false)
  private int[][] field;

  @Column(nullable = false)
  private boolean isPlayerTurn;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private GameStatus status;

  @Column(name = "player_x_id")
  private UUID playerXId;

  @Column(name = "player_o_id")
  private UUID playerOId;

  @Column(name = "player_x_symbol", length = 1)
  private char playerXSymbol;

  @Column(name = "player_o_symbol", length = 1)
  private char playerOSymbol;

  @Column(name = "current_player_id")
  private UUID currentPlayerId;

  @Column(nullable = false, name = "versus_computer")
  private boolean vsComputer;

  @Column(nullable = false, name = "created_at")
  private Instant createdAt;

  public GameEntity() {}

  public GameEntity(
      UUID id,
      int[][] field,
      boolean isPlayerTurn,
      GameStatus status,
      UUID playerXId,
      UUID playerOId,
      char playerXSymbol,
      char playerOSymbol,
      UUID currentPlayerId,
      boolean vsComputer,
      Instant createdAt) {
    this.id = id;
    this.field = field;
    this.isPlayerTurn = isPlayerTurn;
    this.status = status;
    this.playerXId = playerXId;
    this.playerOId = playerOId;
    this.playerXSymbol = playerXSymbol;
    this.playerOSymbol = playerOSymbol;
    this.currentPlayerId = currentPlayerId;
    this.vsComputer = vsComputer;
    this.createdAt = createdAt;
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

  public char getPlayerXSymbol() {
    return playerXSymbol;
  }

  public void setPlayerXSymbol(char playerXSymbol) {
    this.playerXSymbol = playerXSymbol;
  }

  public char getPlayerOSymbol() {
    return playerOSymbol;
  }

  public void setPlayerOSymbol(char playerOSymbol) {
    this.playerOSymbol = playerOSymbol;
  }

  public UUID getCurrentPlayerId() {
    return currentPlayerId;
  }

  public void setCurrentPlayerId(UUID currentPlayerId) {
    this.currentPlayerId = currentPlayerId;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public int[][] getField() {
    return field;
  }

  public void setField(int[][] field) {
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

  public void setVsComputer(boolean vsComputer) {
    this.vsComputer = vsComputer;
  }

  public boolean isVsComputer() {
    return vsComputer;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
