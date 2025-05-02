package com.fazlyev.service;

import com.fazlyev.dto.LogInDto;
import com.fazlyev.model.User;
import com.fazlyev.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    // для проверки пароля

    public boolean authenticate(LogInDto logInDto) {
        Optional<User> userOpt = userRepository.findByEmail(logInDto.getEmail());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // проверяем пароль с зашифрованным паролем в базе
            return passwordEncoder.matches(logInDto.getPassword(), user.getPassword());
        }
        return false;
    }
}
