package com.example.AP1_Jv_T05B.domain.service;

import com.example.AP1_Jv_T05B.domain.entity.Game;
import java.awt.Point;

public interface GameLogic {

  Point getNextMove(Game game);

  GameStatus isGameOver(Game game);
}
