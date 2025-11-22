package com.example.AP1_Jv_T05B.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.AP1_Jv_T05B.datasource.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
class GameIntegrationTest {

  @Autowired MockMvc mockMvc;

  @Autowired UserRepository userRepository;

  private static String token1;
  private static String token2;

  private static final ObjectMapper mapper = new ObjectMapper();

  private static String randomLogin1;
  private static String randomLogin2;

  @Test
  void contextLoads() {}

  @Test
  @Order(1)
  void registerAndLoginUser1() throws Exception {
    randomLogin1 = "user_" + UUID.randomUUID().toString().substring(0, 8);

    // Register first user
    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"login": "%s", "password": "12345"}
                """
                        .formatted(randomLogin1)))
        .andExpect(status().isCreated());

    // Login
    MvcResult result =
        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                {"login": "%s", "password": "12345"}
                """
                            .formatted(randomLogin1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andReturn();

    token1 = mapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
  }

  @Test
  @Order(2)
  void registerAndLoginUser2() throws Exception {
    randomLogin2 = "user_" + UUID.randomUUID().toString().substring(0, 8);

    // Register second user
    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"login": "%s", "password": "12345"}
                """
                        .formatted(randomLogin2)))
        .andExpect(status().isCreated());

    // Login
    MvcResult result =
        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                {"login": "%s", "password": "12345"}
                """
                            .formatted(randomLogin2)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").exists())
            .andReturn();

    token2 = mapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
  }

  @Test
  @Order(3)
  void createPveGame() throws Exception {
    mockMvc
        .perform(
            post("/games")
                .header("Authorization", "Bearer " + token1)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {"vsComputer": true}
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.vsComputer").value(true))
        .andExpect(jsonPath("$.playerXId").isNotEmpty());
  }

  @Test
  @Order(4)
  void joinGame() throws Exception {
    // User1 creates game
    MvcResult result =
        mockMvc
            .perform(
                post("/games")
                    .header("Authorization", "Bearer " + token1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                {"vsComputer": false}
                """))
            .andExpect(status().isCreated())
            .andReturn();

    String gameId = mapper.readTree(result.getResponse().getContentAsString()).get("id").asText();

    // User2 joins
    mockMvc
        .perform(
            post("/games/join")
                .header("Authorization", "Bearer " + token2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"gameId": "%s"}
                """.formatted(gameId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.playerOId").isNotEmpty());
  }

  @Test
  @Order(5)
  void updateGame() throws Exception {
    // User1 creates game
    MvcResult result =
        mockMvc
            .perform(
                post("/games")
                    .header("Authorization", "Bearer " + token1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                {"vsComputer": false}
                """))
            .andExpect(status().isCreated())
            .andReturn();

    String gameId = mapper.readTree(result.getResponse().getContentAsString()).get("id").asText();

    // send correct int[][]
    mockMvc
        .perform(
            put("/games/" + gameId)
                .header("Authorization", "Bearer " + token1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                  "updatedField": [
                    [1,0,0],
                    [0,0,0],
                    [0,0,0]
                  ]
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.field[0][0]").value(1));
  }

  @Test
  @Order(6)
  void getAvailableGames() throws Exception {
    mockMvc
        .perform(get("/games/available").header("Authorization", "Bearer " + token1))
        .andExpect(status().isOk());
  }

  @Test
  @Order(7)
  void getFinishedGames() throws Exception {
    mockMvc
        .perform(get("/games/finished").header("Authorization", "Bearer " + token1))
        .andExpect(status().isOk());
  }

  @Test
  @Order(8)
  void leaderboardReturnsOk() throws Exception {
    mockMvc
        .perform(
            get("/games/leaderboard")
                .param("limit", "10")
                .header("Authorization", "Bearer " + token1))
        .andExpect(status().isOk());
  }
}
