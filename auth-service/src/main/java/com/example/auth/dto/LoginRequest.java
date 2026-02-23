package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO для аутентификации пользователя.
 * Содержит данные для входа: электронную почту и пароль.
 */
@Schema(description = "Запрос на аутентификацию пользователя")
public record LoginRequest(

    @Schema(
        description = "Адрес электронной почты пользователя",
        example = "user@example.com"
    )
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    String email,

    @Schema(
        description = "Пароль пользователя (минимум 6 символов)",
        example = "password123",
        minLength = 6
    )
    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать не менее 6 символов")
    String password
) {}