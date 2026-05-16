package com.example.bank.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SignUpRequest {
    private String username;   // 對應 username
    private String password;   // 明文密碼
    private String realName;   // 對應 real_name
    private String email;      // 對應 email
}
