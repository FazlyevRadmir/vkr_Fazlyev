package com.fazlyev.controller;

import com.fazlyev.model.Project;
import com.fazlyev.service.ProjectService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/projects")
public class ProjectViewController {
    private final ProjectService projectService;

    public ProjectViewController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/{id}")
    public String showProject(@PathVariable String id, Model model, Authentication authentication)
            throws ExecutionException, InterruptedException {
        Project project = projectService.getProjectById(id);
        if (project == null) {
            return "redirect:/error";
        }

        // Добавляем userRating если пользователь авторизован
        if (authentication != null && authentication.isAuthenticated()) {
            Integer userRating = project.getUserRatings() != null ?
                    project.getUserRatings().get(authentication.getName()) :
                    null;
            model.addAttribute("userRating", userRating);
        }

        model.addAttribute("project", project);
        return "project/show";
    }

    @GetMapping
    public String showAllProjects(Model model) throws ExecutionException, InterruptedException {
        model.addAttribute("projects", projectService.getAllProjects());
        return "project/list";
    }

    @GetMapping("/feed")
    public String showProjectFeed(Model model) throws ExecutionException, InterruptedException {
        List<Project> allProjects = projectService.getAllProjects();
        model.addAttribute("projects", allProjects);
        return "project/feed"; // Шаблон для ленты
    }
}