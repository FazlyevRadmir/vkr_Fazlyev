package com.fazlyev.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
public class ProfileForm {
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate; // Изменили на Date для Firebase
    private String telegram;
    private String github;
    private String university;
    private Integer course;
}