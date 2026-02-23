package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO для обновления токена аутентификации.
 * Содержит refresh-токен, используемый для получения новой пары access/refresh токенов.
 */
@Schema(description = "Запрос на обновление access-токена")
public record RefreshTokenRequest(

    @Schema(
        description = "Refresh-токен",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.xxxxx"
    )
    @NotBlank(message = "Refresh-токен обязателен")
    String refreshToken
) {}