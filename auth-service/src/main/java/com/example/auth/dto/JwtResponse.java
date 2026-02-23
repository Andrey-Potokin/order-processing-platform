package com.example.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для ответа при аутентификации или обновлении токена.
 * Содержит пару JWT-токенов: access-токен и refresh-токен.
 */
@Schema(description = "Ответ с JWT-токенами")
public record JwtResponse(

    @Schema(
        description = "Access-токен (JWT), используется для аутентификации запросов",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.xxxxx"
    )
    String accessToken,

    @Schema(
        description = "Refresh-токен, используется для получения нового access-токена",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.yyyyy"
    )
    String refreshToken
) {}