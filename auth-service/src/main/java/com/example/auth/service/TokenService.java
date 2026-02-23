package com.example.auth.service;

import com.example.auth.dto.JwtResponse;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.security.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Сервис для работы с JWT-токенами.
 * Централизует создание и обновление токенов.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenService {

    JwtUtil jwtUtil;
    UserRepository userRepository;
    RefreshTokenService refreshTokenService;

    /**
     * Генерирует пару токенов (access + refresh) для пользователя.
     * Используется при регистрации и логине.
     */
    public Mono<JwtResponse> generateTokens(User user) {
        return refreshTokenService.createRefreshToken(user)
                .map(refreshToken -> new JwtResponse(
                        jwtUtil.generateToken(user),
                        refreshToken.getToken()
                ));
    }

    /**
     * Обновляет access-токен по валидному refresh-токену.
     */
    public Mono<JwtResponse> refresh(String refreshTokenValue) {
        return refreshTokenService.findByToken(refreshTokenValue)
                .flatMap(refreshTokenService::verifyExpiration)
                .flatMap(refreshToken -> userRepository.findById(refreshToken.getUserId())
                        .flatMap(user -> refreshTokenService.createRefreshToken(user)
                                .map(newRefreshToken -> new JwtResponse(
                                        jwtUtil.generateToken(user),
                                        newRefreshToken.getToken()
                                ))
                        )
                )
                .switchIfEmpty(Mono.empty());
    }
}