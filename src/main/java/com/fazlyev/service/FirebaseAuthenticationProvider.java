package com.fazlyev.service;

import com.fazlyev.util.FirebaseErrorTranslator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class FirebaseAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            // 1. Проверяем существование пользователя
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);

            // 2. Аутентифицируем через REST API
            String apiKey = "AIzaSyAlMk-gLNH7MidDHKV3elb03J7p1QzJm9g"; // Замените на реальный ключ из Firebase Console
            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                    email, password);

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            try {
                ResponseEntity<Map> response = new RestTemplate().postForEntity(url, request, Map.class);
                if (response.getStatusCode() == HttpStatus.OK) {
                    return new FirebaseUserAuthentication(
                            email,
                            userRecord.getUid(),
                            List.of(new SimpleGrantedAuthority("ROLE_USER")),
                            (String) response.getBody().get("idToken")
                    );
                } else {
                    throw new BadCredentialsException("Ошибка аутентификации: " + response.getBody());
                }
            } catch (RestClientException e) {
                throw new BadCredentialsException("Ошибка аутентификации: " + e.getMessage());
            }
        } catch (FirebaseAuthException e) {
            throw new BadCredentialsException("Пользователь не найден");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}