package com.example.AP1_Jv_T05B.datasource.mapper;

import com.example.AP1_Jv_T05B.datasource.entity.UserEntity;
import com.example.AP1_Jv_T05B.domain.entity.User;

public class UserMapper {

  public static User toDomain(UserEntity entity) {
    return new User(entity.getId(), entity.getLogin(), entity.getPassword(), entity.getRoles());
  }

  public static UserEntity toEntity(User model) {
    return new UserEntity(model.login(), model.password(), model.roles());
  }
}
