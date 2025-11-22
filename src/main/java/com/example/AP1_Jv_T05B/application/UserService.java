package com.example.AP1_Jv_T05B.application;

import com.example.AP1_Jv_T05B.datasource.entity.UserEntity;
import com.example.AP1_Jv_T05B.datasource.mapper.UserMapper;
import com.example.AP1_Jv_T05B.datasource.repository.UserRepository;
import com.example.AP1_Jv_T05B.domain.entity.Role;
import com.example.AP1_Jv_T05B.domain.entity.User;
import com.example.AP1_Jv_T05B.exception.UserNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder encoder;

  public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
    this.userRepository = userRepository;
    this.encoder = encoder;
  }

  @Transactional(readOnly = true)
  public User getUserById(UUID id) {
    return userRepository
        .findById(id)
        .map(UserMapper::toDomain)
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  @Transactional
  public void register(String login, String password) {
    String encoded = encoder.encode(password);
    UserEntity entity = new UserEntity(login, encoded, List.of(Role.USER));
    userRepository.save(entity);
  }

  @Transactional(readOnly = true)
  public Optional<User> authenticate(String login, String password) {
    return userRepository
        .findByLogin(login)
        .filter(u -> encoder.matches(password, u.getPassword()))
        .map(UserMapper::toDomain);
  }
}
