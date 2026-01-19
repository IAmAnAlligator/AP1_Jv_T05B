package com.example.AP1_Jv_T05B.datasource.repository;

import com.example.AP1_Jv_T05B.datasource.entity.GameEntity;
import com.example.AP1_Jv_T05B.domain.service.GameStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<GameEntity, UUID> {

  List<GameEntity> findAllByStatusIn(List<GameStatus> statuses, Sort sort);

  @Query(
      """
              SELECT g FROM GameEntity g
              WHERE\s
                (g.status = 'DRAW' AND (g.playerXId = :userId OR g.playerOId = :userId))
                OR (g.status = 'PLAYER_X_WON' AND g.playerXId = :userId)
                OR (g.status = 'PLAYER_O_WON' AND g.playerOId = :userId)
                ORDER BY g.createdAt DESC
          """)
  List<GameEntity> findFinishedGamesByUser(UUID userId);

  @Query(
      value =
          """
              SELECT u.id AS user_id,
                     u.login AS login,
                     SUM(s.wins)   AS wins,
                     SUM(s.losses) AS losses,
                     SUM(s.draws)  AS draws,
                     (SUM(s.wins) * 1.0) / (SUM(s.losses) + SUM(s.draws) + 1) AS ratio
              FROM users u
              JOIN (
                  SELECT player_x_id AS user_id,
                         CASE WHEN status = 'PLAYER_X_WON' THEN 1 ELSE 0 END AS wins,
                         CASE WHEN status = 'PLAYER_O_WON' THEN 1 ELSE 0 END AS losses,
                         CASE WHEN status = 'DRAW'         THEN 1 ELSE 0 END AS draws
                  FROM games

                  UNION ALL

                  SELECT player_o_id AS user_id,
                         CASE WHEN status = 'PLAYER_O_WON' THEN 1 ELSE 0 END AS wins,
                         CASE WHEN status = 'PLAYER_X_WON' THEN 1 ELSE 0 END AS losses,
                         CASE WHEN status = 'DRAW'         THEN 1 ELSE 0 END AS draws
                  FROM games
              ) s ON u.id = s.user_id
              GROUP BY u.id, u.login
              ORDER BY ratio DESC
              LIMIT :limit
              """,
      nativeQuery = true)
  List<Object[]> findLeaderboard(int limit);
}
