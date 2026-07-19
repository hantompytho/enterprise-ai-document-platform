package com.alexpetro.eadp.dto;

import java.time.LocalDateTime;
import com.alexpetro.eadp.entity.Role;

public class UserResponse {

    private final Long id;
    private final String email;
    private final Role role;
    private final LocalDateTime createdAt;

    public UserResponse(Long id, String email, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}