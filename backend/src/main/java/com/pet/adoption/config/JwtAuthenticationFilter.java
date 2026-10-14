package com.pet.adoption.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    final JwtUtil jwt;
    final CustomUserDetailsService uds;

    public JwtAuthenticationFilter(JwtUtil jwt, CustomUserDetailsService uds) {
        this.jwt = jwt;
        this.uds = uds;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        try {
            String b = req.getHeader("Authorization");
            if (StringUtils.hasText(b) && b.startsWith("Bearer ")
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = jwt.parseUsername(b.substring(7));
                if (StringUtils.hasText(username)) {
                    UserDetails u = uds.loadUserByUsername(username);
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(u, null, u.getAuthorities()));
                }
            }
        } catch (Exception ignored) {
            // token 解析失败静默放行，由 SecurityConfig 决定是否 401
        }
        chain.doFilter(req, res);
    }
}