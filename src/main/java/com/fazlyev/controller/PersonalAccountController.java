package com.fazlyev.controller;

import com.fazlyev.service.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class PersonalAccountController {
    private final ProjectService projectService;

    public PersonalAccountController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/personalAccount")
    public String personalAccount(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            String userEmail = principal.getName();
            model.addAttribute("email", userEmail);
            model.addAttribute("projects", projectService.getProjectsByUserEmail(userEmail));
        } catch (ExecutionException | InterruptedException e) {
            model.addAttribute("projects", List.of());
            e.printStackTrace();
        }

        return "personalAccount";
    }
}