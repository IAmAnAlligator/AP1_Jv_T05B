package com.example.AP1_Jv_T05B.web.mapper;

import com.example.AP1_Jv_T05B.domain.entity.User;
import com.example.AP1_Jv_T05B.web.dto.UserResponse;

public class UserMapper {

  private UserMapper() {} // запрещаем создание экземпляра

  public static UserResponse toDTO(User user) {
    return new UserResponse(user.id(), user.login(), user.roles());
  }
}
