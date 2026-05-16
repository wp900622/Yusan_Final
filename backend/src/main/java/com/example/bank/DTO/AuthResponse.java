package com.example.bank.DTO;

import com.example.bank.Entity.Role;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.management.ConstructorParameters;

@Data
@Getter
@Setter

public class AuthResponse {
    private String token;
    private String username;
    private String realName;
    private Role role;

    // 全參數建構子 (方便在 Controller 快速 New 出物件)
    public AuthResponse(String token, String username, String realName, Role role) {
        this.token = token;
        this.username = username;
        this.realName = realName;
        this.role = role;
    }
}
