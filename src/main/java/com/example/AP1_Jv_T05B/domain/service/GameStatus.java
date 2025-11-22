package com.example.AP1_Jv_T05B.domain.service;

public enum GameStatus {
  WAITING, // игра создана, ждёт второго игрока (PvP)
  PLAYER_TURN, // ход игрока (PvP)
  PLAYER_WON, // победа игрока (PvP)
  COMPUTER_WON, // победа компьютера (PvE)
  DRAW, // ничья
  IN_PROGRESS, // игра идёт
  PLAYER_X_TURN,
  PLAYER_O_TURN,
  PLAYER_X_WON,
  PLAYER_O_WON
}
