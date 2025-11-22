package com.example.AP1_Jv_T05B.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.AP1_Jv_T05B.datasource.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  private static String randomUser1;
  private static String randomUser2;

  @Test
  void contextLoads() {}

  @Test
  @Order(1)
  void registerReturns201() throws Exception {
    randomUser1 = "user_" + UUID.randomUUID().toString().substring(0, 8);

    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"login": "%s", "password": "12345"}
                """
                        .formatted(randomUser1)))
        .andExpect(status().isCreated());
  }

  @Test
  @Order(2)
  void loginReturnsTokens() throws Exception {
    randomUser2 = "user_" + UUID.randomUUID().toString().substring(0, 8);

    // Сначала зарегистрируем пользователя
    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"login": "%s", "password": "12345"}
                """
                        .formatted(randomUser2)))
        .andExpect(status().isCreated());

    // Теперь логинимся
    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {"login": "%s", "password": "12345"}
                """
                        .formatted(randomUser2)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.refreshToken").exists());
  }
}
