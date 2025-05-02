package com.fazlyev.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogInDto {

    @NotBlank(message = "Email не может быть пустым")
    private String email;  // оставляем email

    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

}
