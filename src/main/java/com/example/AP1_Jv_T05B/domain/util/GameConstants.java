package com.example.AP1_Jv_T05B.domain.util;

import java.util.UUID;

public final class GameConstants {

  private GameConstants() {}

  public static final int BOARD_SIZE = 3;
  public static final int PLAYER_MARK = 1;
  public static final int COMPUTER_MARK = -1;
  public static final int EMPTY_CELL = 0;
  public static final int COMPUTER_WIN_SCORE = 10;
  public static final int PLAYER_WIN_SCORE = -10;

  public static final int PLAYER_X_MARK = 1;
  public static final int PLAYER_O_MARK = -1;

  public static final UUID COMPUTER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
}
