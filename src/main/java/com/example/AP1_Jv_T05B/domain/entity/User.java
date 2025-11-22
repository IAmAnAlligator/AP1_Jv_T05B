package com.example.AP1_Jv_T05B.domain.entity;

import java.util.List;
import java.util.UUID;

public record User(UUID id, String login, String password, List<Role> roles) {}
