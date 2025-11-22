package com.example.AP1_Jv_T05B.web.mapper;

import com.example.AP1_Jv_T05B.domain.entity.Game;
import com.example.AP1_Jv_T05B.web.dto.GameResponse;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class GameMapper {

  private GameMapper() {}

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

  public static GameResponse toDTO(Game game) {
    return new GameResponse(
        game.getId(),
        game.getGameField().getField(),
        game.getStatus(),
        game.getPlayerXId(),
        game.getPlayerOId(),
        game.getPlayerXMark(),
        game.getPlayerOMark(),
        game.isVsComputer(),
        FORMATTER.format(game.getCreatedAt()));
  }
}
