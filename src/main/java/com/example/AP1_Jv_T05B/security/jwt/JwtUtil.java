package com.example.AP1_Jv_T05B.security.jwt;

import io.jsonwebtoken.Claims;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class JwtUtil {

  /** Создает JwtAuthentication на основе Claims из токена */
  public static JwtAuthentication fromClaims(Claims claims) {

    // Извлекаем UUID из subject
    UUID principal = UUID.fromString(claims.getSubject());

    // Извлекаем роли, предполагая что claims содержит "roles": ["USER", "ADMIN"]
    List<String> roles =
        claims.get("roles") != null
            ? ((List<?>) claims.get("roles")).stream().map(Object::toString).toList()
            : List.of(); // если ролей нет, возвращаем пустой список

    Collection<? extends GrantedAuthority> authorities =
        roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

    // В момент создания считаем, что пользователь авторизован
    return new JwtAuthentication(principal, authorities, true);
  }
}
