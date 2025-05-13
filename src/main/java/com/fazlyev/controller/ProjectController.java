   package com.fazlyev.controller;

import com.fazlyev.model.*;
import com.fazlyev.service.*;

import com.google.common.net.HttpHeaders;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;
    private final FileStorageService fileStorageService;
    private final CategoryService categoryService;

    public ProjectController(ProjectService projectService,
                             FileStorageService fileStorageService,
                             CategoryService categoryService) {
        this.projectService = projectService;
        this.fileStorageService = fileStorageService;
        this.categoryService = categoryService;
    }
    

    @GetMapping("/new")
    public String newProjectForm(Model model) throws ExecutionException, InterruptedException {
        // Убедитесь, что имя атрибута "projectForm" совпадает с тем, что используется в форме
        model.addAttribute("projectForm", new ProjectForm());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "project/new";
    }

    @PostMapping
    public String createProject(
            @ModelAttribute ProjectForm form,
            Authentication authentication)
            throws IOException, ExecutionException, InterruptedException {

        // 1. Загрузка файлов в Firebase Storage
        List<String> screenshotUrls = fileStorageService.uploadFiles(
                form.getScreenshots().toArray(new MultipartFile[0]), "screenshots");
        String mainFileUrl = fileStorageService.uploadFile(form.getMainFile(), "mainFiles");
        List<String> additionalFileUrls = form.getAdditionalFiles() != null ?
                fileStorageService.uploadFiles(
                        form.getAdditionalFiles().toArray(new MultipartFile[0]), "additionalFiles") :
                null;

        // 2. Создание объекта проекта
        Project project = new Project();
        project.setTitle(form.getTitle());
        project.setDescription(form.getDescription());
        project.setCategory(form.getCategory());
        project.setAuthorId(getCurrentUserId(authentication));
        project.setCreatedAt(new Date());
        project.setScreenshotUrls(screenshotUrls);
        project.setMainFileUrl(mainFileUrl);
        project.setAdditionalFileUrls(additionalFileUrls);

        // 3. Сохранение в Firebase Firestore
        projectService.saveProject(project);

        return "redirect:/projects/" + project.getId();
    }

    private String getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName(); // Возвращает email пользователя
        }
        return "anonymous";
    }

    @GetMapping("/personalAccount")
    public String personalAccount(Model model, Authentication authentication) throws ExecutionException, InterruptedException {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String userEmail = authentication.getName();
        List<Project> userProjects = projectService.getProjectsByUserEmail(userEmail);

        model.addAttribute("projects", userProjects);
        model.addAttribute("user", authentication.getPrincipal());

        return "personalAccount";
    }

    @PostMapping("/{projectId}/rate")
    @ResponseBody
    public ResponseEntity<?> rateProject(
            @PathVariable String projectId,
            @RequestParam int rating,
            Authentication authentication) throws ExecutionException, InterruptedException {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Требуется авторизация"));
        }

        String userId = authentication.getName();
        projectService.rateProject(projectId, rating, userId);

        return ResponseEntity.ok(Map.of(
                "message", "Оценка сохранена",
                "newRating", projectService.getProjectById(projectId).getAverageRating()
        ));
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        Path filePath = Paths.get("path/to/files/" + fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("Файл не найден: " + fileName);
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


    @PostMapping("/{projectId}/comment")
    public String addComment(
            @PathVariable String projectId,
            @RequestParam String text,
            Authentication authentication) throws ExecutionException, InterruptedException {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Comment comment = new Comment();
        comment.setAuthor(authentication.getName());
        comment.setText(text);
        comment.setDate(new Date());

        projectService.addComment(projectId, comment);
        return "redirect:/projects/" + projectId;
    }

    @GetMapping("/image/{fileName:.+}")
    public ResponseEntity<byte[]> getImage(
            @PathVariable String fileName,
            Authentication authentication) throws IOException {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String filePath = "screenshots/" + fileName; // или другой путь
        byte[] imageBytes = fileStorageService.downloadFile(filePath);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // или MediaType.IMAGE_PNG
                .body(imageBytes);
    }

}