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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegistrationDto());
        return "registration"; // Возвращаем форму регистрации
    }

    @PostMapping
    public String registerUser(
            @Valid @ModelAttribute("user") RegistrationDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // Проверка на совпадение паролей
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "passwords.not.match", "Пароли не совпадают");
        }

        // Если есть ошибки в валидации, возвращаем обратно на форму
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        try {
            // Если ошибок нет, регистрируем пользователя
            userService.registerUser(dto);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/registration?success";
        } catch (IllegalArgumentException e) {
            // Если ошибка при регистрации, отображаем ошибку для поля email
            bindingResult.rejectValue("email", "email.exists", e.getMessage());
            return "registration";
        }
    }
}
