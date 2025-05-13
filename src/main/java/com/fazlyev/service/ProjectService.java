package com.fazlyev.service;

import com.fazlyev.model.Project;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fazlyev.model.Comment;
import com.google.cloud.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ProjectService implements ProjectServiceInterface {
    private final Firestore firestore;

    @Autowired
    public ProjectService(Firestore firestore) {
        this.firestore = firestore;
    }

    public Project saveProject(Project project) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("projects").document();
        project.setId(docRef.getId());
        ApiFuture<WriteResult> result = docRef.set(project);
        result.get();
        return project;
    }

    public List<Project> getProjectsByUserEmail(String userEmail) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = firestore.collection("projects")
                .whereEqualTo("authorId", userEmail)
                .get();

        List<QueryDocumentSnapshot> documents = query.get().getDocuments();
        List<Project> projects = new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            Project project = document.toObject(Project.class);
            project.setId(document.getId());
            projects.add(project);
        }

        return projects;
    }

    public Project getProjectById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("projects").document(id);
        DocumentSnapshot document = docRef.get().get();
        if (document.exists()) {
            Project project = document.toObject(Project.class);
            project.setId(document.getId());
            return project;
        }
        return null;
    }

    public List<Project> getAllProjects() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> query = firestore.collection("projects").get();
        return query.get().getDocuments().stream()
                .map(document -> {
                    Project project = document.toObject(Project.class);
                    project.setId(document.getId());
                    return project;
                })
                .collect(Collectors.toList());
    }

    public void rateProject(String projectId, int rating, String userId)
            throws ExecutionException, InterruptedException {

        DocumentReference docRef = firestore.collection("projects").document(projectId);
        DocumentSnapshot snapshot = docRef.get().get();

        if (!snapshot.exists()) {
            throw new IllegalArgumentException("Проект не найден");
        }

        // Получаем текущие данные
        double currentRating = snapshot.contains("averageRating") ?
                snapshot.getDouble("averageRating") : 0.0;
        int ratingCount = snapshot.contains("ratingCount") ?
                snapshot.getLong("ratingCount").intValue() : 0;
        Map<String, Integer> userRatings = snapshot.contains("userRatings") ?
                (Map<String, Integer>) snapshot.get("userRatings") : new HashMap<>();

        // Если пользователь уже оценивал
        if (userRatings.containsKey(userId)) {
            int oldRating = userRatings.get(userId);
            currentRating = (currentRating * ratingCount - oldRating + rating) / ratingCount;
        } else {
            currentRating = (currentRating * ratingCount + rating) / (ratingCount + 1);
            ratingCount++;
        }

        // Обновляем данные пользователя
        userRatings.put(userId, rating);

        // Сохраняем в Firestore
        docRef.update(
                "averageRating", currentRating,
                "ratingCount", ratingCount,
                "userRatings", userRatings
        ).get();
    }

    @Override
    public void addComment(String projectId, Comment comment) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("projects").document(projectId);
        docRef.update("comments", FieldValue.arrayUnion(comment)).get();
    }

}