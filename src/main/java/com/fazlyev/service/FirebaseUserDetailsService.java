package com.fazlyev.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class FirebaseUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            com.google.firebase.auth.UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);

            return User.builder()
                    .username(userRecord.getEmail())
                    .password("")
                    .roles("USER")
                    .build();
        } catch (FirebaseAuthException e) {
            throw new UsernameNotFoundException("Пользователь не найден", e);
        }
    }
}
