package com.khoatrbl.productivity.services;

import com.khoatrbl.productivity.domains.entities.Users;

import java.util.UUID;

public interface RefreshTokenService {
    String createRefreshToken(Users currentUser);

    Users validateAndExtend(String rawToken);

    void revoke(String rawToken);
}
