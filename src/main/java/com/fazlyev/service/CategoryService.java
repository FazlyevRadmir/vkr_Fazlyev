package com.fazlyev.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import jdk.jfr.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CategoryService {
    private final Firestore firestore;

    @Autowired
    public CategoryService(Firestore firestore) {
        this.firestore = firestore;
    }

    public List<Category> getAllCategories() throws ExecutionException, InterruptedException {
        return firestore.collection("categories").get().get()
                .toObjects(Category.class);
    }
}