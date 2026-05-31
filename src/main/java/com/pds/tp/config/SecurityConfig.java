package com.pds.tp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    private final HeaderRoleAuthenticationFilter headerRoleAuthenticationFilter;
    private final RateLimitingFilter rateLimitingFilter;

    public SecurityConfig(HeaderRoleAuthenticationFilter headerRoleAuthenticationFilter,
                          RateLimitingFilter rateLimitingFilter) {
        this.headerRoleAuthenticationFilter = headerRoleAuthenticationFilter;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/auth/register", "/v1/api/auth/register",
                                "/api/auth/login", "/v1/api/auth/login",
                                "/api/auth/*/verify-email", "/v1/api/auth/*/verify-email",
                                "/swagger-ui/**", "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers(
                                "/api/scrims/*/acciones/*", "/v1/api/scrims/*/acciones/*",
                                "/api/scrims/*/cancelar", "/v1/api/scrims/*/cancelar")
                        .hasAnyRole("MOD", "ADMIN")
                        .requestMatchers(
                                "/api/scrims/*/reportes", "/v1/api/scrims/*/reportes")
                        .hasAnyRole("USER", "MOD", "ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .addFilterBefore(rateLimitingFilter, AnonymousAuthenticationFilter.class)
                .addFilterBefore(headerRoleAuthenticationFilter, AnonymousAuthenticationFilter.class);

        return http.build();
    }
}
