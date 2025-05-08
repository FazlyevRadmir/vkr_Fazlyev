package com.fazlyev.controller;

import com.fazlyev.model.LoginForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class PersonalAccountController {
    @GetMapping("/personalAccount")
    public String personalAccount(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login"; // Перенаправление, если нет аутентификации
        }
        model.addAttribute("email", principal.getName());
        return "personalAccount";
    }
}