package com.example.AP1_Jv_T05B.web.controller;

import com.example.AP1_Jv_T05B.application.AuthorizationService;
import com.example.AP1_Jv_T05B.security.dto.JwtRequest;
import com.example.AP1_Jv_T05B.security.dto.JwtResponse;
import com.example.AP1_Jv_T05B.security.dto.RefreshJwtRequest;
import com.example.AP1_Jv_T05B.security.jwt.JwtAuthentication;
import com.example.AP1_Jv_T05B.web.dto.MessageResponse;
import com.example.AP1_Jv_T05B.web.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthorizationService authorizationService;

  public AuthController(AuthorizationService authorizationService) {
    this.authorizationService = authorizationService;
  }

  @PostMapping("/register")
  public ResponseEntity<MessageResponse> register(@RequestBody @Valid JwtRequest jwtRequest) {
    authorizationService.register(jwtRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new MessageResponse("User registered successfully"));
  }

  /** Авторизация пользователя (Basic auth → Tokens) */
  @PostMapping("/login")
  public ResponseEntity<JwtResponse> login(@RequestBody @Valid JwtRequest jwtRequest) {
    JwtResponse response = authorizationService.login(jwtRequest);
    return ResponseEntity.ok(response);
  }

  /** Обновление accessToken по refreshToken */
  @PostMapping("/token/refresh-access")
  public ResponseEntity<JwtResponse> refreshAccess(@RequestBody @Valid RefreshJwtRequest refresh) {
    JwtResponse newAccessToken = authorizationService.refreshAccessToken(refresh);
    return ResponseEntity.ok(newAccessToken);
  }

  /** Обновление refreshToken и accessToken по refreshToken */
  @PostMapping("/token/refresh")
  public ResponseEntity<JwtResponse> refreshTokens(@RequestBody @Valid RefreshJwtRequest refresh) {
    JwtResponse response = authorizationService.refreshTokens(refresh);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getCurrentUser() {
    JwtAuthentication auth = authorizationService.getAuthentication();
    UserResponse response = authorizationService.getUserById(auth.getPrincipal());
    return ResponseEntity.ok(response);
  }
}
