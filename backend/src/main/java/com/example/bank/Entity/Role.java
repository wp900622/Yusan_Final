package com.example.bank.Entity;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    ADMIN
    ;


    @Override
    public @Nullable String getAuthority() {
        return "ROLE_" + this.name();
    }
}