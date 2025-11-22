package com.example.AP1_Jv_T05B.security.jwt;

import com.example.AP1_Jv_T05B.domain.entity.Role;
import com.example.AP1_Jv_T05B.domain.entity.User;
import com.example.AP1_Jv_T05B.exception.InvalidJwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

  private String lastJwtError;

  @Value("${jwt.access-token-expiration}")
  private long accessTokenExpiration;

  @Value("${jwt.refresh-token-expiration}")
  private long refreshTokenExpiration;

  @Value("${jwt.access-secret}")
  private String jwtAccessSecret;

  @Value("${jwt.refresh-secret}")
  private String jwtRefreshSecret;

  private Key getAccessKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
  }

  private Key getRefreshKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
  }

  /** Генерация accessToken по пользователю */
  public String generateAccessToken(User user) {
    Date now = new Date();
    Date expiration = new Date(now.getTime() + accessTokenExpiration);

    return Jwts.builder()
        .setSubject(user.id().toString())
        .claim("roles", user.roles().stream().map(Role::name).toList())
        .setIssuedAt(now)
        .setExpiration(expiration)
        .signWith(getAccessKey())
        .compact();
  }

  /** Генерация refreshToken по пользователю */
  public String generateRefreshToken(User user) {
    Date now = new Date();
    Date expiration = new Date(now.getTime() + refreshTokenExpiration);

    return Jwts.builder()
        .setSubject(user.id().toString())
        .setIssuedAt(now)
        .setExpiration(expiration)
        .signWith(getRefreshKey())
        .compact();
  }

  /** Проверка валидности accessToken */
  public boolean validateAccessToken(String token) {
    return validateToken(token, getAccessKey());
  }

  /** Проверка валидности refreshToken */
  public boolean validateRefreshToken(String token) {
    return validateToken(token, getRefreshKey());
  }

  private boolean validateToken(String token, Key secret) {
    lastJwtError = null; // сбрасываем ошибку

    try {
      Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token);

      return true;
    } catch (ExpiredJwtException e) {
      lastJwtError = "JWT expired: " + e.getMessage();
    } catch (UnsupportedJwtException e) {
      lastJwtError = "Unsupported JWT: " + e.getMessage();
    } catch (MalformedJwtException e) {
      lastJwtError = "Malformed JWT: " + e.getMessage();
    } catch (Exception e) {
      lastJwtError = "Invalid token: " + e.getMessage();
    }

    return false;
  }

  public Claims parseRefreshToken(String refreshToken) {
    if (!validateRefreshToken(refreshToken)) {
      throw new InvalidJwtException(
          lastJwtError != null ? lastJwtError : "Invalid or expired refresh token");
    }
    return getRefreshClaims(refreshToken);
  }

  /** Извлечь claims из accessToken */
  public Claims getAccessClaims(String token) {
    return getClaims(token, getAccessKey());
  }

  /** Извлечь claims из refreshToken */
  public Claims getRefreshClaims(String token) {
    return getClaims(token, getRefreshKey());
  }

  private Claims getClaims(String token, Key secret) {
    try {
      return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
    } catch (JwtException e) {
      throw new InvalidJwtException("Failed to parse JWT: " + e.getMessage());
    }
  }
}
