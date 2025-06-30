package com.fazlyev.service;

import com.fazlyev.model.Comment;
import com.fazlyev.model.Project;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ProjectServiceInterface {
    void addComment(String projectId, Comment comment) throws ExecutionException, InterruptedException;
    void rateProject(String projectId, int rating, String userId) throws ExecutionException, InterruptedException;
}