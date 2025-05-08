package com.fazlyev.util;

import com.google.firebase.auth.FirebaseAuthException;

public class FirebaseErrorTranslator {

    public static String translateAuthError(FirebaseAuthException e) {
        String errorCode = String.valueOf(e.getErrorCode());
        if (errorCode == null) {
            return "Ошибка аутентификации";
        }

        return switch (errorCode) {
            case "user-not-found" -> "Пользователь с таким email не существует";
            case "invalid-email" -> "Некорректный email адрес";
            case "wrong-password" -> "Неверный пароль";
            case "email-already-in-use", "EMAIL_EXISTS" -> "Пользователь с таким email уже зарегистрирован";
            case "weak-password", "WEAK_PASSWORD" -> "Пароль слишком простой (минимум 6 символов)";
            case "operation-not-allowed", "OPERATION_NOT_ALLOWED" -> "Регистрация с email/паролем отключена";
            default -> "Ошибка аутентификации: " + e.getMessage();
        };
    }

    public static String translateRegistrationError(FirebaseAuthException e) {
        String errorCode = String.valueOf(e.getErrorCode());
        if (errorCode == null) {
            return "Ошибка регистрации";
        }

        return switch (errorCode) {
            case "EMAIL_EXISTS" -> "Пользователь с таким email уже зарегистрирован";
            case "INVALID_EMAIL" -> "Некорректный email адрес";
            case "WEAK_PASSWORD" -> "Пароль слишком простой (минимум 6 символов)";
            case "OPERATION_NOT_ALLOWED" -> "Регистрация с email/паролем отключена";
            default -> "Ошибка регистрации: " + e.getMessage();
        };
    }
}