package com.example.bank.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="users")
@Getter
@Setter
public class UserEntity {
    @Id
    @Column(name = "user_id", length = 50)
    private String userId; // 主鍵

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password; // 儲存加密後的密碼（BCrypt 剛好需要 varchar(255)）

    @Column(name = "real_name", length = 100)
    private String realName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "role", length = 20)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER; // 預設值為 USER

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


}
