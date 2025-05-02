package com.fazlyev.controller;

import com.fazlyev.dto.LogInDto;
import com.fazlyev.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDto", new LogInDto());  // создаем объект LogInDto
        return "login";  // возвращаем имя шаблона формы
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute("loginDto") LogInDto loginDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "login";  // если есть ошибки валидации, возвращаем на форму
        }

        boolean isAuthenticated = authenticationService.authenticate(loginDto);
        if (isAuthenticated) {
            // Если аутентификация прошла успешно:
            return "redirect:/personalAccount";  // перенаправление на личный кабинет
        } else {
            // Если аутентификация не прошла, возвращаем на страницу логина с сообщением об ошибке
            model.addAttribute("error", "Неверный email или пароль");
            return "login";
        }
    }
}
