package com.pet.adoption.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pet.adoption.entity.User;
import com.pet.adoption.mapper.UserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    final UserMapper userMapper;

    public CustomUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (u == null) throw new UsernameNotFoundException("用户不存在");
        return new org.springframework.security.core.userdetails.User(
                u.username, u.password, !Boolean.TRUE.equals(u.disabled),
                true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + u.role))
        );
    }
}