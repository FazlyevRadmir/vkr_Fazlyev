package com.fazlyev.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationDto {
    @NotBlank(message = "Имя обязательно")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    private String lastName;

    @Email(message = "Некорректный email")
    @NotBlank(message = "Email обязателен")
    private String email;

    @Size(min = 8, message = "Пароль должен быть минимум 8 символов")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Пароль должен содержать заглавные, строчные буквы и цифры")
    private String password;

    private boolean agreeToTerms; // Для галочки "Я согласен"
}