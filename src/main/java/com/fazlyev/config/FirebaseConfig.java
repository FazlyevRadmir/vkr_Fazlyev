package com.fazlyev.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials.path}")
    private String credentialsPath;

    @PostConstruct
    public void initialize() throws IOException {
        try (InputStream serviceAccount = new ClassPathResource(credentialsPath).getInputStream()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("vkrfazlyev.firebasestorage.app")
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully");
            }
        } catch (Exception e) {
            log.error("Firebase initialization failed", e);
            throw new IllegalStateException("Failed to initialize Firebase", e);
        }
    }

    @Bean
    public Firestore firestore() {
        try {
            Firestore firestore = FirestoreClient.getFirestore();
            log.info("Firestore initialized successfully");
            return firestore;
        } catch (Exception e) {
            log.error("Failed to initialize Firestore", e);
            throw new IllegalStateException("Failed to initialize Firestore", e);
        }
    }

    @Bean
    public Storage storage() {
        try {
            Storage storage = StorageClient.getInstance().bucket().getStorage();
            log.info("Firebase Storage initialized successfully");
            return storage;
        } catch (Exception e) {
            log.error("Failed to initialize Firebase Storage", e);
            throw new IllegalStateException("Failed to initialize Firebase Storage. Make sure bucket exists.", e);
        }
    }
}