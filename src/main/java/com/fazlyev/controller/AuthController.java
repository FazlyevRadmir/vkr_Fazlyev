//package com.fazlyev.controller;
//
//import com.fazlyev.dto.RegistrationDto;
//import com.fazlyev.service.UserService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//@Controller
//@RequestMapping("/auth") // Добавляем базовый путь
//public class AuthController {
//
//    @GetMapping("/register")
//    public String showRegistrationForm(Model model) {
//        model.addAttribute("user", new RegistrationDto());
//        return "auth/register"; // Соответствует пути к файлу
//    }
//
//    @PostMapping("/register")
//    public String registerUser(@Valid @ModelAttribute("user") RegistrationDto dto,
//                               BindingResult result) {
//        if (result.hasErrors()) {
//            return "auth/register";
//        }
//        // Логика регистрации
//        return "redirect:/auth/login";
//    }
//}