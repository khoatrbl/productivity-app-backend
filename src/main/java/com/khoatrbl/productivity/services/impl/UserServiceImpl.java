package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.dtos.UpdateCoinsRequest;
import com.khoatrbl.productivity.domains.dtos.UpdateExpRequest;
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
import jakarta.transaction.Transactional;
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
        Users existingUser = userRepository.findByIdForUpdate(id)
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
        Users currentUser = userRepository.findByIdForUpdate(id)
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
    @Transactional
    public Users updateUserLevel(UUID id, UpdateExpRequest updateExpRequest) {
        int gain = updateExpRequest.getExpGained();
        if (gain <= 0) {
            throw new IllegalArgumentException("EXP gained must be positive.");
        }

        // Lock the row: task completion, quote reward and start-XP can land at the same time
        Users currentUser = userRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + id));

        applyExp(currentUser, gain);
        return currentUser; // managed entity; flushed on commit
    }

    /**
     * Adds EXP and levels up as many times as needed.
     * Returns the number of levels gained (0 = no level-up).
     */
    private int applyExp(Users user, int gainedExp) {
        Level level = user.getCurrentLevel();
        int exp = user.getCurrentExp() + gainedExp;
        int levelsGained = 0;

        while (exp >= level.getThreshold()) {
            Optional<Level> next = levelRepository.findByLevel(level.getLevel() + 1);
            if (next.isEmpty()) {
                exp = level.getThreshold(); // max level: cap the bar at full
                break;
            }
            exp -= level.getThreshold();
            level = next.get();
            levelsGained++;
        }

        user.setCurrentLevel(level);
        user.setCurrentExp(exp);
        return levelsGained;
    }

    @Override
    public Users updateUserCoins(UUID id, UpdateCoinsRequest updateCoinsRequest) {
        Users user = userRepository.findByIdForUpdate(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found for id: " + id)
                );

        int currentCoins = user.getCoins();

        user.setCoins(currentCoins + updateCoinsRequest.getAmount());
        return userRepository.save(user);
    }
}
