package com.example.AP1_Jv_T05B.datasource.repository;

import com.example.AP1_Jv_T05B.datasource.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, UUID> {

  Optional<UserEntity> findByLogin(String login);
}
