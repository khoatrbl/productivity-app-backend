package com.khoatrbl.productivity.security;

import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // because the users log in using their emails, the username will be the email in this case

        Users user = userRepository.findByEmail(username)
                .orElseThrow(
                        () -> new EntityNotFoundException("User not found with email: " + username)
                );

        return new CustomUserDetails(user);
    }
}
