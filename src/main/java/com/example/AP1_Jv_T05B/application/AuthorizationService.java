package com.example.AP1_Jv_T05B.application;

import com.example.AP1_Jv_T05B.domain.entity.User;
import com.example.AP1_Jv_T05B.exception.InvalidCredentialsException;
import com.example.AP1_Jv_T05B.security.dto.JwtRequest;
import com.example.AP1_Jv_T05B.security.dto.JwtResponse;
import com.example.AP1_Jv_T05B.security.dto.RefreshJwtRequest;
import com.example.AP1_Jv_T05B.security.jwt.JwtAuthentication;
import com.example.AP1_Jv_T05B.security.jwt.JwtProvider;
import com.example.AP1_Jv_T05B.security.jwt.JwtUtil;
import com.example.AP1_Jv_T05B.web.dto.UserResponse;
import io.jsonwebtoken.Claims;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorizationService {

  private static final String BEARER = "Bearer";

  private final UserService userService;
  private final JwtProvider jwtProvider;

  public AuthorizationService(UserService userService, JwtProvider jwtProvider) {
    this.userService = userService;
    this.jwtProvider = jwtProvider;
  }

  @Transactional
  public void register(JwtRequest request) {
    userService.register(request.login(), request.password());
  }

  @Transactional(readOnly = true)
  public UserResponse getUserById(UUID id) {
    User user = userService.getUserById(id);
    return new UserResponse(user.id(), user.login(), user.roles());
  }

  /** Авторизация: Basic credentials → Tokens */
  @Transactional(readOnly = true)
  public JwtResponse login(JwtRequest jwtRequest) {
    // ищем пользователя по логину
    User user =
        userService
            .authenticate(jwtRequest.login(), jwtRequest.password())
            .orElseThrow(InvalidCredentialsException::new);

    // генерируем токены
    String accessToken = jwtProvider.generateAccessToken(user);
    String refreshToken = jwtProvider.generateRefreshToken(user);

    return new JwtResponse(BEARER, accessToken, refreshToken);
  }

  /** Обновляет только accessToken по refreshToken */
  @Transactional(readOnly = true)
  public JwtResponse refreshAccessToken(RefreshJwtRequest request) {
    // Парсим refreshToken
    Claims claims = jwtProvider.parseRefreshToken(request.refreshToken());

    // Создаём JwtAuthentication безопасно (без ролей)
    JwtAuthentication auth = JwtUtil.fromClaims(claims);

    // Получаем пользователя по ID
    User user = userService.getUserById(auth.getPrincipal());

    String newAccessToken = jwtProvider.generateAccessToken(user);

    // Генерируем новый accessToken
    return new JwtResponse(BEARER, newAccessToken, request.refreshToken());
  }

  /** Обновляет и accessToken, и refreshToken */
  @Transactional(readOnly = true)
  public JwtResponse refreshTokens(RefreshJwtRequest request) {
    Claims claims = jwtProvider.parseRefreshToken(request.refreshToken());
    JwtAuthentication auth = JwtUtil.fromClaims(claims);

    User user = userService.getUserById(auth.getPrincipal());

    String newAccessToken = jwtProvider.generateAccessToken(user);
    String newRefreshToken = jwtProvider.generateRefreshToken(user);

    return new JwtResponse(BEARER, newAccessToken, newRefreshToken);
  }

  /** Возвращает текущую JwtAuthentication из контекста */
  @Transactional(readOnly = true)
  public JwtAuthentication getAuthentication() {
    return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
  }
}
