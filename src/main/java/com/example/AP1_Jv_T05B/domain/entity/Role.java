package com.example.AP1_Jv_T05B.domain.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
  USER;

  @Override
  public String getAuthority() {
    return "ROLE_" + name();
  }
}
