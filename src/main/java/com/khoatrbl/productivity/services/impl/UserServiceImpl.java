package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.repositories.UserRepository;
import com.khoatrbl.productivity.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Users getUserById(UUID id) {
        Optional<Users> user = userRepository.findById(id);

        return user.orElseThrow(() -> new EntityNotFoundException("User not found for id: " + id));
    }

    @Override
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }
}
