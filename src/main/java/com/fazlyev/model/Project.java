package com.fazlyev.model;

import lombok.Data;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Project {
    private String id;
    private String title;
    private String description;
    private String category;
    private String authorId;
    private double averageRating = 0.0;
    private int ratingCount = 0;
    private Map<String, Integer> userRatings = new HashMap<>();
    private List<Comment> comments;
    private Date createdAt = new Date();
    private List<String> screenshotUrls;
    private String mainFileUrl;
    private List<String> additionalFileUrls;
}