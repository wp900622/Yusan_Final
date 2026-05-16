package com.example.bank.Service;


import com.example.bank.Entity.UserEntity;
import com.example.bank.Repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public @NonNull User loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("帳號不存在: " + username));

        // 讀取你在資料庫設定的角色欄位 (加上 "ROLE_" 前綴是 Spring Security 的標準規範)
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userEntity.getRole());

        // 建立並回傳 UserDetails 物件
        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(), // 這裡拿出來的是加密密碼，Security 會自動用它比對
                Collections.singletonList(authority)
        );
    }
}
