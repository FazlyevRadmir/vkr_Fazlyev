package com.fazlyev.service;

import com.fazlyev.model.ProfileForm;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class ProfileService {
    public void saveProfile(String userId, ProfileForm profileForm) throws ExecutionException, InterruptedException {
        System.out.println("Saving profile for UID: " + userId);

        Firestore db = FirestoreClient.getFirestore();

        Date birthDate = profileForm.getBirthDate() != null ?
                Date.from(profileForm.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant()) :
                null;

        // Обновляем напрямую поля документа (без подколлекции profile)
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", profileForm.getFirstName());
        updates.put("lastName", profileForm.getLastName());
        updates.put("birthDate", birthDate);
        updates.put("telegram", profileForm.getTelegram());
        updates.put("github", profileForm.getGithub());
        updates.put("university", profileForm.getUniversity());
        updates.put("course", profileForm.getCourse());

        try {
            db.collection("users")
                    .document(userId)
                    .set(updates, SetOptions.merge())
                    .get();
            System.out.println("Profile saved successfully");
        } catch (Exception e) {
            System.err.println("Error saving profile: " + e.getMessage());
            throw e;
        }
    }

    public ProfileForm getProfile(String userId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        DocumentSnapshot document = db.collection("users")
                .document(userId)
                .get()
                .get();

        if (!document.exists()) {
            return new ProfileForm();
        }

        ProfileForm form = new ProfileForm();
        form.setFirstName(document.getString("firstName"));
        form.setLastName(document.getString("lastName"));
        form.setTelegram(document.getString("telegram"));
        form.setGithub(document.getString("github"));
        form.setUniversity(document.getString("university"));
        form.setCourse(document.getLong("course") != null ?
                document.getLong("course").intValue() : null);

        Date birthDate = document.getDate("birthDate");
        if (birthDate != null) {
            form.setBirthDate(birthDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());
        }

        return form;
    }
}