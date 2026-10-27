package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.entities.RefreshToken;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.exceptions.InvalidRefreshTokenException;
import com.khoatrbl.productivity.repositories.RefreshTokenRepository;
import com.khoatrbl.productivity.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom;

    private static final int TOKEN_BYTE = 32;


    @Override
    public String createRefreshToken(Users currentUser) {
        byte[] bytes = new byte[TOKEN_BYTE];
        secureRandom.nextBytes(bytes);

        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        String hashToken = hash(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(hashToken)
                .user(currentUser)
                .build();

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Override
    @Transactional
    public Users validateAndExtend(String rawToken) {
        Instant now = Instant.now();

        // one method is to retrieve all token, and then perform .matches on each entry until we find a correct one
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .filter(t -> t.isUsable(now))
                .orElseThrow(
                        () -> new InvalidRefreshTokenException("Received invalid refresh token.")
                );

        token.extend(now);

        return token.getUser();
    }

    @Override
    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenHash(hash(rawToken))
                .ifPresent(token -> token.revoke(Instant.now()));
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
