package com.khoatrbl.productivity.services;

import com.khoatrbl.productivity.domains.dtos.RegisterRequest;
import com.khoatrbl.productivity.domains.entities.Users;

import java.util.List;
import java.util.UUID;

public interface UserService {
    Users getUserById(UUID id);
    Users getUserByEmail(String email);
    List<Users> getAllUsers();
}
