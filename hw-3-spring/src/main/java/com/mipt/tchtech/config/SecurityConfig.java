package com.mipt.tchtech.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.tchtech.security.JwtAuthFilter;
import com.mipt.tchtech.security.PepperedBCryptPasswordEncoder;
import com.mipt.tchtech.security.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.pepper}")
    private String pepper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthFilter jwtAuthFilter,
                                           RestAuthenticationEntryPoint entryPoint,
                                           ObjectMapper objectMapper) throws Exception {
        http
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/external/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/profile").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/v1/docs").hasAuthority("READ_PRIVILEGE")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(e -> e
                .authenticationEntryPoint(entryPoint)
                .accessDeniedHandler((request, response, ex) -> {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    Map<String, Object> body = new LinkedHashMap<>();
                    body.put("timestamp", Instant.now().toString());
                    body.put("status", 403);
                    body.put("error", "Forbidden");
                    body.put("message", "Доступ запрещён — недостаточно прав");
                    body.put("path", request.getRequestURI());
                    objectMapper.writeValue(response.getWriter(), body);
                })
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PepperedBCryptPasswordEncoder(pepper);
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .authorities("ROLE_USER")
                .build();

        UserDetails reader = User.builder()
                .username("reader")
                .password(passwordEncoder.encode("password"))
                .authorities("ROLE_USER", "READ_PRIVILEGE")
                .build();

        return new InMemoryUserDetailsManager(user, reader);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}