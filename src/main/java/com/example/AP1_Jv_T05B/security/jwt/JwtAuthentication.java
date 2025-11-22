package com.example.AP1_Jv_T05B.security.jwt;

import java.util.Collection;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthentication implements Authentication {

  private final UUID principal;
  private final Collection<? extends GrantedAuthority> authorities;
  private boolean authenticated;

  public JwtAuthentication(
      UUID principal, Collection<? extends GrantedAuthority> authorities, boolean authenticated) {
    this.principal = principal;
    this.authorities = authorities;
    this.authenticated = authenticated;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities; // Возвращаем роли
  }

  @Override
  public Object getCredentials() {
    return null; // JWT не хранит пароль после валидации
  }

  @Override
  public Object getDetails() {
    return null; // Можно хранить payload токена, если нужно
  }

  @Override
  public UUID getPrincipal() {
    return principal; // Возвращаем UUID как principal
  }

  @Override
  public String getName() {
    return principal.toString(); // Имя = UUID
  }

  @Override
  public boolean isAuthenticated() {
    return authenticated; // Факт авторизации
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    this.authenticated = isAuthenticated;
  }
}
