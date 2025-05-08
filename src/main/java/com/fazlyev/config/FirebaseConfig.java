package com.fazlyev.config;

import com.fazlyev.controller.RegistrationController;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);
    @PostConstruct
    public void init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = getClass().getResourceAsStream("/firebase-service-account.json");

                if (serviceAccount == null) {
                    throw new RuntimeException("Файл firebase-service-account.json не найден");
                }

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase успешно инициализирован");
            }
        } catch (IOException e) {
            log.error("Ошибка инициализации Firebase", e);
            throw new RuntimeException("Ошибка инициализации Firebase", e);
        }
    }
}