package com.fazlyev.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationDto {
    @NotBlank(message = "Имя обязательно")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    private String lastName;

    @Email(message = "Введите действительный email")
    @NotBlank(message = "Email обязателен")
    private String email;

    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Пароль должен содержать заглавные и строчные буквы, а также цифры")
    private String password;

    @AssertTrue(message = "Необходимо принять условия")
    private boolean agreeToTerms;
}