package com.fazlyev.controller;

import com.fazlyev.dto.RegistrationDto;
import com.fazlyev.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") RegistrationDto dto, BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "auth/register";
        }

        if (!dto.isAgreeToTerms()) {
            redirectAttributes.addFlashAttribute("error", "Необходимо принять условия");
            return "redirect:/register";
        }

        userService.registerUser(dto);
        redirectAttributes.addFlashAttribute("success", "Регистрация успешна!");
        return "redirect:/login";
    }
}