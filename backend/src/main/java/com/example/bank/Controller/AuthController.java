package com.example.bank.Controller;

import com.example.bank.DTO.AuthResponse;
import com.example.bank.DTO.SignUpRequest;
import com.example.bank.Entity.Role;
import com.example.bank.Entity.UserEntity;
import com.example.bank.Repository.UserRepository;
import com.example.bank.util.JwtUtil;
import com.example.bank.util.XssSanitizer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        // 1. 至資料庫檢查帳號是否存在
        UserEntity userEntity = userRepository.findByUsername(username).orElse(null);
        if (userEntity == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("帳號或密碼錯誤");
        }

        // 2. 比對加密密碼
        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("帳號或密碼錯誤");
        }

        // 3. 驗證通過，核發 JWT Token (把 username 放進 Token 中)
        String token = jwtUtils.generateToken(userEntity.getUsername());

        // 4. 回傳 Token 及使用者基本資料給前端
        AuthResponse authResponse = new AuthResponse(
                token,
                userEntity.getUsername(),
                userEntity.getRealName(),
                userEntity.getRole()
        );

        return ResponseEntity.ok(authResponse);
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest request) {
        // 1. 對可顯示欄位做 XSS 淨化；email 由 @Email 驗證格式即可，不過 sanitize 以免 @ 被轉成 &#64;
        String username = XssSanitizer.clean(request.getUsername());
        String realName = XssSanitizer.clean(request.getRealName());
        String email    = request.getEmail();

        // 2. 檢查帳號 (Username) 是否已經被註冊過了
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("此帳號已被佔用");
        }

        // 3. 建立新的 User 實體
        UserEntity newUser = new UserEntity();
        String randomId = UUID.randomUUID().toString();
        newUser.setUserId(randomId);
        newUser.setUsername(username);
        newUser.setRealName(realName);
        newUser.setEmail(email);
        // 帳號為 "admin" 自動授予管理者角色；其他人預設為一般使用者
        newUser.setRole("admin".equals(username) ? Role.ADMIN : Role.USER);

        // 4. 【超級關鍵】將明文密碼加密後再存入！
        // 這樣存進去才會是像 $2a$10$... 這樣的安全雜湊碼
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        newUser.setPassword(hashedPassword);

        // 5. 存入資料庫
        userRepository.save(newUser);

        return ResponseEntity.ok("註冊成功！請重新登入。");
    }
}
