package com.fazlyev.service;

import com.fazlyev.dto.RegistrationDto;
import com.fazlyev.model.Role;
import com.fazlyev.model.User;
import com.fazlyev.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email уже используется");
        }

        User user = new User();
        user.setFirstName(dto.getFirstName());  // Используем getFirstName()
        user.setLastName(dto.getLastName());    // Используем getLastName()
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.STUDENT);  // Или другое значение по умолчанию

        userRepository.save(user);
    }
}