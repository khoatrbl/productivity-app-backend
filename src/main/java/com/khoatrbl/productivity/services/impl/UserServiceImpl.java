package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.dtos.UpdatePasswordRequest;
import com.khoatrbl.productivity.domains.dtos.UpdateProfileRequest;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.exceptions.EmailAlreadyExistsException;
import com.khoatrbl.productivity.exceptions.PasswordsNotMatchException;
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
    public Users updateUserPassword(UUID id, UpdatePasswordRequest updatePasswordRequest) {
        Users currentUser = userRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found for id: " + id)
                );

        // TODO: These hashes are being hashed differently. Fix.
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

        return userRepository.save(currentUser);
    }
}
