package io.sultanov.taskmanagementsystem.security.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
    ADMINISTRATOR("ADMINISTRATOR"),
    USER("USER");

    private final String role;

    @Override
    public String getAuthority() {
        return role;
    }
}

