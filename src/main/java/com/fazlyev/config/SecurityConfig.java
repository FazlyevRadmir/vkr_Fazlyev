package com.fazlyev.config;

import com.fazlyev.service.FirebaseAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.naming.AuthenticationException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .invalidSessionUrl("/login?invalid-session")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/registration", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email") // Указываем, что email используется как username
                        .defaultSuccessUrl("/personalAccount", true)
                        .failureUrl("/login?error")
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                )
                .authenticationProvider(firebaseAuthenticationProvider()); // Добавляем кастомный провайдер

        return http.build();
    }

    @Bean
    public FirebaseAuthenticationProvider firebaseAuthenticationProvider() {
        return new FirebaseAuthenticationProvider();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // You can choose a different encoder if needed
    }
}