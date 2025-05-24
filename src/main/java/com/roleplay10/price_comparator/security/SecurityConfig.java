package com.roleplay10.price_comparator.security;

import com.roleplay10.price_comparator.service.JpaUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final JpaUserDetailsService uds;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        var jwtFilter = new JwtAuthenticationFilter(jwtUtil, uds);

        http
                // disable CSRF for a stateless API
                .csrf(AbstractHttpConfigurer::disable)
                // no session; every request must be authenticated via JWT
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // authorization rules
                .authorizeHttpRequests(auth -> auth
                        // allow anonymous access to register & login
                        .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login").permitAll()
                        // everything else requires a valid JWT
                        .anyRequest().authenticated()
                )

                // add our JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
