package com.roleplay10.price_comparator.service;

import com.roleplay10.price_comparator.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {
    private final AppUserRepository userRepo;
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        var user = userRepo.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("No user " + username));
        return User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(List.of())
                .build();
    }

}