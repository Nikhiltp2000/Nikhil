package com.atdxt;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SecurityService implements UserDetailsService {

    private final AuthRepository authRepository;


    public SecurityService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Auth userEncrypt = authRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String password = userEncrypt.getPassword(); // Get the password from Auth entity

        return User.withUsername(username)
                .password(password)
                .roles("ADMIN")
                .build();
    }



}
