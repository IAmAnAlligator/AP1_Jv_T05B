package com.example.AP1_Jv_T05B.unit.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.AP1_Jv_T05B.application.UserService;
import com.example.AP1_Jv_T05B.datasource.entity.UserEntity;
import com.example.AP1_Jv_T05B.datasource.mapper.UserMapper;
import com.example.AP1_Jv_T05B.datasource.repository.UserRepository;
import com.example.AP1_Jv_T05B.domain.entity.Role;
import com.example.AP1_Jv_T05B.domain.entity.User;
import com.example.AP1_Jv_T05B.exception.UserNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private BCryptPasswordEncoder encoder;

  private UserService userService;

  private UUID userId;
  private String login;
  private String password;

  @BeforeEach
  void setUp() {
    userService = new UserService(userRepository, encoder);

    userId = UUID.randomUUID();
    login = "alex";
    password = "password";
  }

  // ---------- getUserById ----------

  @Test
  void getUserById_UserExists_ReturnsUser() {
    UserEntity entity = new UserEntity();
    entity.setId(userId);
    entity.setLogin(login);
    entity.setRoles(List.of(Role.USER));

    when(userRepository.findById(userId)).thenReturn(Optional.of(entity));

    try (MockedStatic<UserMapper> mapper = mockStatic(UserMapper.class)) {
      User domainUser = new User(userId, login, null, List.of(Role.USER));
      mapper.when(() -> UserMapper.toDomain(entity)).thenReturn(domainUser);

      User result = userService.getUserById(userId);

      assertEquals(userId, result.id());
      assertEquals(login, result.login());
      assertEquals(List.of(Role.USER), result.roles());
    }
  }

  @Test
  void getUserById_UserNotFound_ThrowsException() {
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
  }

  // ---------- register ----------

  @Test
  void register_DelegatesToRepositoryWithEncodedPassword() {
    String encodedPassword = "encodedPassword";
    when(encoder.encode(password)).thenReturn(encodedPassword);

    userService.register(login, password);

    verify(userRepository)
        .save(
            argThat(
                entity ->
                    entity.getLogin().equals(login)
                        && entity.getPassword().equals(encodedPassword)
                        && entity.getRoles().contains(Role.USER)));
  }

  // ---------- authenticate ----------

  @Test
  void authenticate_CorrectPassword_ReturnsUser() {
    UserEntity entity = new UserEntity();
    entity.setId(userId);
    entity.setLogin(login);
    entity.setPassword("encoded");
    entity.setRoles(List.of(Role.USER));

    when(userRepository.findByLogin(login)).thenReturn(Optional.of(entity));
    when(encoder.matches(password, "encoded")).thenReturn(true);

    try (MockedStatic<UserMapper> mapper = mockStatic(UserMapper.class)) {
      User domainUser = new User(userId, login, null, List.of(Role.USER));
      mapper.when(() -> UserMapper.toDomain(entity)).thenReturn(domainUser);

      Optional<User> result = userService.authenticate(login, password);

      assertTrue(result.isPresent());
      assertEquals(userId, result.get().id());
    }
  }

  @Test
  void authenticate_WrongPassword_ReturnsEmpty() {
    UserEntity entity = new UserEntity();
    entity.setId(userId);
    entity.setLogin(login);
    entity.setPassword("encoded");

    when(userRepository.findByLogin(login)).thenReturn(Optional.of(entity));
    when(encoder.matches(password, "encoded")).thenReturn(false);

    Optional<User> result = userService.authenticate(login, password);

    assertTrue(result.isEmpty());
  }

  @Test
  void authenticate_UserNotFound_ReturnsEmpty() {
    when(userRepository.findByLogin(login)).thenReturn(Optional.empty());

    Optional<User> result = userService.authenticate(login, password);

    assertTrue(result.isEmpty());
  }
}
