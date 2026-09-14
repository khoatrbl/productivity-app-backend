package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.dtos.UpdateLevelRequest;
import com.khoatrbl.productivity.domains.dtos.UpdatePasswordRequest;
import com.khoatrbl.productivity.domains.dtos.UpdateProfileRequest;
import com.khoatrbl.productivity.domains.entities.Level;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.exceptions.EmailAlreadyExistsException;
import com.khoatrbl.productivity.exceptions.PasswordsNotMatchException;
import com.khoatrbl.productivity.repositories.LevelRepository;
import com.khoatrbl.productivity.repositories.UserRepository;
import com.khoatrbl.productivity.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final PasswordEncoder passwordEncoder;

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
    public Users updateUserProfile(UUID id, UpdateProfileRequest updateProfileRequest) {
        Users existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + id));

        if (userRepository.existsByEmailAndIdNot(updateProfileRequest.getEmail(), id)) {
            throw new EmailAlreadyExistsException("Email is already used.");
        }

        existingUser.setEmail(updateProfileRequest.getEmail());
        existingUser.setDisplayName(updateProfileRequest.getDisplayName());

        return userRepository.save(existingUser);
    }

    @Override
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void updateUserPassword(UUID id, UpdatePasswordRequest updatePasswordRequest) {
        Users currentUser = userRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found for id: " + id)
                );

        String oldPassword = updatePasswordRequest.getOldPassword();
        String newPassword = updatePasswordRequest.getNewPassword();
        String confirmPassword = updatePasswordRequest.getConfirmNewPassword();

        if (!passwordEncoder.matches(oldPassword, currentUser.getPasswordHash())) {
            throw new PasswordsNotMatchException("Old password not matched.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordsNotMatchException("New password not matched.");
        }

        String hashedNewPassword = passwordEncoder.encode(newPassword);

        currentUser.setPasswordHash(hashedNewPassword);

        userRepository.save(currentUser);
    }

    @Override
    public Users updateUserLevel(UUID id, UpdateLevelRequest updateLevelRequest) {
        Users currentUser = userRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found for id: " + id)
                );

        Level currentUserLevel = currentUser.getCurrentLevel();
        int currentExp = currentUser.getCurrentExp();
        int gain = updateLevelRequest.getExpGained();
        int maxXpOfLevel = levelRepository.findByLevel(currentUserLevel.getLevel())
                .orElseThrow(
                        () -> new EntityNotFoundException("Level not found for level: " + currentUserLevel.getLevel())
                ).getThreshold();

        int remainExp = 0;

        // If EXP gain + current EXP exceeds threshold for current level
        if ((maxXpOfLevel - currentExp) <= gain) {
            remainExp = gain - (maxXpOfLevel - currentExp);

            int nextLevelValue = currentUserLevel.getLevel() + 1;

            Level nextLevel = levelRepository.findByLevel(nextLevelValue)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Level not found for level: " + nextLevelValue)
                    );

            currentUser.setCurrentLevel(nextLevel);
            currentUser.setCurrentExp(remainExp);
        } else {
            currentUser.setCurrentExp(currentExp + gain);
        }

        return userRepository.save(currentUser);
    }
}
