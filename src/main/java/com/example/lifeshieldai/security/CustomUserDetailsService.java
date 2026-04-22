package com.example.lifeshieldai.security;

import com.example.lifeshieldai.entity.User;
import com.example.lifeshieldai.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("ユーザーが見つかりません");
        }
        return new AuthenticatedUser(user.getId(), user.getName(), user.getEmail(), user.getPasswordHash(), user.getRole());
    }
}
