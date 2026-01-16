package com.example.AP1_Jv_T05B.web.controller;

import com.example.AP1_Jv_T05B.application.UserService;
import com.example.AP1_Jv_T05B.domain.entity.User;
import com.example.AP1_Jv_T05B.web.dto.UserResponse;
import com.example.AP1_Jv_T05B.web.mapper.UserMapper;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  // --- получение пользователя по UUID ---
  @GetMapping("/{userId}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {
    User user = userService.getUserById(userId);
    return ResponseEntity.ok(UserMapper.toDTO(user));
  }

//  @GetMapping("/forbidden-test")
//  @PreAuthorize("denyAll()")
//  public String forbidden() {
//    return "never";
//  }
}
