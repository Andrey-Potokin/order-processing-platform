package com.example.auth.controller;

import com.example.auth.dto.JwtResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RefreshTokenRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.service.AuthService;
import com.example.auth.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST-контроллер для обработки аутентификации и управления сессиями.
 * <p>
 * Предоставляет эндпоинты:
 * <ul>
 *   <li>Регистрация нового пользователя</li>
 *   <li>Вход в систему (логин)</li>
 *   <li>Обновление access-токена с помощью refresh-токена</li>
 * </ul>
 * </p>
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication", description = "API для регистрации, входа и обновления токенов")
public class AuthController {

    AuthService authService;
    TokenService tokenService;

    /**
     * Регистрирует нового пользователя.
     * <p>
     * При успешной регистрации возвращается JWT-токен (access + refresh).
     * Если пользователь с таким email уже существует — возвращается статус 409.
     * </p>
     *
     * @param request DTO с полями {@code email} и {@code password}
     * @return ResponseEntity с телом {@link JwtResponse} и статусом 200 при успехе,
     *         или 409 при конфликте (пользователь уже существует)
     */
    @PostMapping("/register")
    @Operation(
        summary = "Регистрация пользователя",
        description = "Создаёт нового пользователя и возвращает JWT-токены",
        responses = {
            @ApiResponse(responseCode = "200", description = "Успешная регистрация",
                content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "409", description = "Пользователь уже существует")
        }
    )
    public Mono<ResponseEntity<JwtResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Запрос на регистрацию пользователя: email={}", request.email());

        return authService.register(request)
                .doOnSuccess(jwtResponse -> log.info("Пользователь успешно зарегистрирован: email={}", request.email()))
                .doOnError(error -> log.warn("Ошибка при регистрации пользователя: email={}, причина={}",
                        request.email(), error.getMessage()))
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    /**
     * Аутентифицирует пользователя по email и паролю.
     * <p>
     * При успешном входе возвращаются access- и refresh-токены.
     * При неверных учётных данных — статус 401.
     * </p>
     *
     * @param request DTO с полями {@code email} и {@code password}
     * @return ResponseEntity с {@link JwtResponse} при успехе, или 401 при ошибке
     */
    @PostMapping("/login")
    @Operation(
        summary = "Вход в систему",
        description = "Аутентифицирует пользователя и возвращает JWT-токены",
        responses = {
            @ApiResponse(responseCode = "200", description = "Успешный вход",
                content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверные учётные данные")
        }
    )
    public Mono<ResponseEntity<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Запрос на вход: email={}", request.email());

        return authService.login(request)
                .doOnSuccess(jwtResponse -> log.info("Пользователь успешно вошёл: email={}", request.email()))
                .doOnError(error -> log.warn("Ошибка при входе: email={}, причина={}",
                        request.email(), error.getMessage()))
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    /**
     * Обновляет access-токен с использованием действительного refresh-токена.
     * <p>
     * Проверяет срок действия refresh-токена. Если он просрочен — возвращается 401.
     * Если токен не найден — также возвращается 401.
     * </p>
     *
     * @param request DTO с полем {@code refreshToken}
     * @return ResponseEntity с новым {@link JwtResponse}, или 401 при ошибке
     */
    @PostMapping("/refresh")
    @Operation(
        summary = "Обновление токена",
        description = "Генерирует новый access-токен по валидному refresh-токену",
        responses = {
            @ApiResponse(responseCode = "200", description = "Токен успешно обновлён",
                content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Токен недействителен или просрочен")
        }
    )
    public Mono<ResponseEntity<JwtResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Запрос на обновление токена");

        return tokenService.refresh(request.refreshToken())
                .doOnSuccess(jwtResponse -> log.info("Токен успешно обновлён"))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Refresh-токен не найден или недействителен");
                    return Mono.empty();
                }))
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }
}