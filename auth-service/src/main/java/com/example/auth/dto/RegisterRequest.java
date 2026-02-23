package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для регистрации нового пользователя.
 * Содержит учётные данные: email и пароль.
 */
@Schema(description = "Запрос на регистрацию пользователя")
public record RegisterRequest(

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Schema(
        description = "Адрес электронной почты пользователя",
        example = "user@example.com"
    )
    String email,

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать не менее 6 символов")
    @Schema(
        description = "Пароль пользователя (минимум 6 символов)",
        example = "password123",
        minLength = 6
    )
    String password
) {}