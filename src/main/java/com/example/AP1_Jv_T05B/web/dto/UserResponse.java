package com.example.AP1_Jv_T05B.web.dto;

import com.example.AP1_Jv_T05B.domain.entity.Role;
import java.util.List;
import java.util.UUID;

public record UserResponse(UUID id, String login, List<Role> roles) {

  public UserResponse(UUID id, String login, List<Role> roles) {
    this.id = id;
    this.login = login;
    // Делаем список immutable
    this.roles = List.copyOf(roles);
  }
}
