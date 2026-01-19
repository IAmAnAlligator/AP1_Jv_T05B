package com.example.AP1_Jv_T05B.unit.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.AP1_Jv_T05B.application.AuthorizationService;
import com.example.AP1_Jv_T05B.application.UserService;
import com.example.AP1_Jv_T05B.domain.entity.Role;
import com.example.AP1_Jv_T05B.domain.entity.User;
import com.example.AP1_Jv_T05B.exception.InvalidCredentialsException;
import com.example.AP1_Jv_T05B.security.dto.JwtRequest;
import com.example.AP1_Jv_T05B.security.dto.JwtResponse;
import com.example.AP1_Jv_T05B.security.dto.RefreshJwtRequest;
import com.example.AP1_Jv_T05B.security.jwt.JwtAuthentication;
import com.example.AP1_Jv_T05B.security.jwt.JwtProvider;
import com.example.AP1_Jv_T05B.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

  @Mock private UserService userService;

  @Mock private JwtProvider jwtProvider;

  private AuthorizationService service;

  private User user;
  private UUID userId;

  @BeforeEach
  void setUp() {
    service = new AuthorizationService(userService, jwtProvider);

    userId = UUID.randomUUID();
    user = new User(userId, "alex", "hashed", List.of(Role.USER));
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  // ---------- register ----------

  @Test
  void register_DelegatesToUserService() {
    JwtRequest request = new JwtRequest("alex", "password");

    service.register(request);

    verify(userService).register("alex", "password");
  }

  // ---------- getUserById ----------

  @Test
  void getUserById_ReturnsUserResponse() {
    when(userService.getUserById(userId)).thenReturn(user);

    var response = service.getUserById(userId);

    assertEquals(userId, response.id());
    assertEquals("alex", response.login());
    assertEquals(user.roles(), response.roles());
  }

  // ---------- login ----------

  @Test
  void login_Success_ReturnsTokens() {
    JwtRequest request = new JwtRequest("alex", "password");

    when(userService.authenticate("alex", "password")).thenReturn(Optional.of(user));

    when(jwtProvider.generateAccessToken(user)).thenReturn("access");
    when(jwtProvider.generateRefreshToken(user)).thenReturn("refresh");

    JwtResponse response = service.login(request);

    assertEquals("Bearer", response.type());
    assertEquals("access", response.accessToken());
    assertEquals("refresh", response.refreshToken());
  }

  @Test
  void login_InvalidCredentials_ThrowsException() {
    JwtRequest request = new JwtRequest("alex", "wrong");

    when(userService.authenticate("alex", "wrong")).thenReturn(Optional.empty());

    assertThrows(InvalidCredentialsException.class, () -> service.login(request));

    verify(jwtProvider, never()).generateAccessToken(any());
    verify(jwtProvider, never()).generateRefreshToken(any());
  }

  // ---------- refreshAccessToken ----------

  @Test
  void refreshAccessToken_ReturnsNewAccessToken() {
    String refreshToken = "refresh-token";
    RefreshJwtRequest request = new RefreshJwtRequest(refreshToken);

    Claims claims = mock(Claims.class);
    JwtAuthentication auth = new JwtAuthentication(userId);

    when(jwtProvider.parseRefreshToken(refreshToken)).thenReturn(claims);
    when(userService.getUserById(userId)).thenReturn(user);
    when(jwtProvider.generateAccessToken(user)).thenReturn("new-access");

    try (MockedStatic<JwtUtil> jwtUtil = mockStatic(JwtUtil.class)) {
      jwtUtil.when(() -> JwtUtil.fromClaims(claims)).thenReturn(auth);

      JwtResponse response = service.refreshAccessToken(request);

      assertEquals("Bearer", response.type());
      assertEquals("new-access", response.accessToken());
      assertEquals(refreshToken, response.refreshToken());
    }
  }

  // ---------- refreshTokens ----------

  @Test
  void refreshTokens_ReturnsNewAccessAndRefreshTokens() {
    String refreshToken = "refresh-token";
    RefreshJwtRequest request = new RefreshJwtRequest(refreshToken);

    Claims claims = mock(Claims.class);
    JwtAuthentication auth = new JwtAuthentication(userId);

    when(jwtProvider.parseRefreshToken(refreshToken)).thenReturn(claims);
    when(userService.getUserById(userId)).thenReturn(user);
    when(jwtProvider.generateAccessToken(user)).thenReturn("new-access");
    when(jwtProvider.generateRefreshToken(user)).thenReturn("new-refresh");

    try (MockedStatic<JwtUtil> jwtUtil = mockStatic(JwtUtil.class)) {
      jwtUtil.when(() -> JwtUtil.fromClaims(claims)).thenReturn(auth);

      JwtResponse response = service.refreshTokens(request);

      assertEquals("Bearer", response.type());
      assertEquals("new-access", response.accessToken());
      assertEquals("new-refresh", response.refreshToken());
    }
  }

  // ---------- getAuthentication ----------

  @Test
  void getAuthentication_ReturnsAuthenticationFromSecurityContext() {
    JwtAuthentication auth = new JwtAuthentication(userId);
    SecurityContext context = mock(SecurityContext.class);

    when(context.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(context);

    JwtAuthentication result = service.getAuthentication();

    assertSame(auth, result);
  }
}
