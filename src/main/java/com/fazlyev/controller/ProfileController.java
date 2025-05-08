package com.fazlyev.controller;

import com.fazlyev.model.ProfileForm;
import com.fazlyev.model.User;
import com.fazlyev.service.FirebaseUserAuthentication;
import com.fazlyev.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/edit")
    public String editProfileForm(Model model) throws ExecutionException, InterruptedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof FirebaseUserAuthentication) {
            FirebaseUserAuthentication token = (FirebaseUserAuthentication) auth;
            String uid = token.getUid();
            String email = token.getName();

            // Загрузка профиля
            ProfileForm profile = profileService.getProfile(uid);
            model.addAttribute("profileForm", profile);
            model.addAttribute("email", email); // Исправлено: было "userEmail", стало "email"

            return "editProfile";
        }

        return "redirect:/login";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute ProfileForm profileForm, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication object: {}", auth);

        if (!(auth instanceof FirebaseUserAuthentication)) {
            log.warn("User is not authenticated with Firebase");
            return "redirect:/login?session_expired";
        }

        FirebaseUserAuthentication token = (FirebaseUserAuthentication) auth;
        String uid = token.getUid();
        log.info("Updating profile for UID: {}", uid);

        try {
            profileService.saveProfile(uid, profileForm);
            return "redirect:/personalAccount?success";
        } catch (Exception e) {
            log.error("Error saving profile", e);
            model.addAttribute("error", "Ошибка сохранения: " + e.getMessage());
            return "editProfile";
        }
    }
}