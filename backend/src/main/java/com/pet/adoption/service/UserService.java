package com.pet.adoption.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pet.adoption.config.JwtUtil;
import com.pet.adoption.dto.PageResult;
import com.pet.adoption.dto.Requests.*;
import com.pet.adoption.entity.User;
import com.pet.adoption.exception.BusinessException;
import com.pet.adoption.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserService {
    final UserMapper userMapper;
    final PasswordEncoder encoder;
    final AuthenticationManager auth;
    final JwtUtil jwt;

    @Value("${app.minio.public-url}")
    String minioPublicUrl;

    public UserService(UserMapper userMapper, PasswordEncoder encoder,
                       AuthenticationManager auth, JwtUtil jwt) {
        this.userMapper = userMapper;
        this.encoder = encoder;
        this.auth = auth;
        this.jwt = jwt;
    }

    public User register(RegisterRequest r) {
        Long cnt = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, r.username));
        if (cnt != null && cnt > 0) throw new BusinessException("用户名已存在");
        User u = new User();
        u.username = r.username;
        u.password = encoder.encode(r.password);
        u.nickname = StringUtils.hasText(r.nickname) ? r.nickname : r.username;
        u.phone = r.phone;
        u.email = r.email;
        u.role = "USER";
        u.avatar = minioPublicUrl + "/common_user.png";
        u.disabled = false;
        userMapper.insert(u);
        return u;
    }


    public LoginResponse login(LoginRequest r) {
        auth.authenticate(new UsernamePasswordAuthenticationToken(r.username, r.password));
        User u = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, r.username));
        if (u == null) throw new BusinessException("用户不存在");
        if (Boolean.TRUE.equals(u.disabled)) throw new BusinessException(403, "账号已被禁用");
        return new LoginResponse(jwt.generate(u.username), u);
    }

    public User currentUserRaw() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User u = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (u == null) throw new BusinessException("用户不存在");
        return u;
    }

    public User currentUser() {
        return currentUserRaw();
    }

    public User updateProfile(UserProfileRequest r) {
        User u = currentUserRaw();
        if (r.nickname != null) u.nickname = r.nickname;
        if (r.phone != null) u.phone = r.phone;
        if (r.email != null) u.email = r.email;
        if (StringUtils.hasText(r.avatar)) u.avatar = r.avatar;
        userMapper.updateById(u);
        return u;
    }

    public PageResult<User> adminSearch(String keyword, String role, Boolean disabled, int page, int size) {
        Page<User> p = new Page<>(Math.max(page, 1), size);
        IPage<User> result = userMapper.search(p,
                StringUtils.hasText(keyword) ? keyword.trim() : null,
                StringUtils.hasText(role) ? role : null,
                disabled);
        return PageResult.of(result);
    }

    public User adminUpdate(Long id, UserAdminRequest r) {
        User u = userMapper.selectById(id);
        if (u == null) throw new BusinessException("用户不存在");
        if (r.nickname != null) u.nickname = r.nickname;
        if (r.phone != null) u.phone = r.phone;
        if (r.email != null) u.email = r.email;
        if (r.role != null) u.role = r.role;
        if (r.avatar != null) u.avatar = r.avatar;
        if (r.disabled != null) u.disabled = r.disabled;
        userMapper.updateById(u);
        return u;
    }

    public void deleteUser(Long id) {
        User u = userMapper.selectById(id);
        if (u == null) throw new BusinessException("用户不存在");
        if ("admin".equals(u.username)) throw new BusinessException("演示管理员账号不能删除");
        userMapper.deleteById(id);
    }

    public void batchDisable(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        List<User> users = userMapper.selectBatchIds(ids);
        for (User u : users) {
            if (!"admin".equals(u.username)) {
                u.disabled = true;
                userMapper.updateById(u);
            }
        }
    }
}