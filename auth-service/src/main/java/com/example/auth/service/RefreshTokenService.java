package com.example.auth.service;

import com.example.auth.entity.RefreshToken;
import com.example.auth.entity.User;
import com.example.auth.repository.RefreshTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сервис для работы с refresh-токенами.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration-days}")
    int refreshExpirationDays;

    final RefreshTokenRepository refreshTokenRepository;

    /**
     * Создаёт новый refresh-токен для пользователя.
     *
     * @param user пользователь, для которого создаётся токен
     * @return Mono с сохранённым объектом {@link RefreshToken}
     */
    public Mono<RefreshToken> createRefreshToken(User user) {
        return Mono.fromSupplier(() ->
            RefreshToken.builder()
                .userId(user.getId())
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(refreshExpirationDays))
                .build()
        ).flatMap(refreshTokenRepository::save);
    }

    /**
     * Находит refresh-токен по его значению.
     *
     * @param token строковое значение токена
     * @return Mono с найденным токеном или пустым, если не найден
     */
    public Mono<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Проверяет, не истёк ли срок действия refresh-токена.
     *
     * @param token токен для проверки
     * @return тот же токен, если он валиден
     * @throws RuntimeException если срок действия истёк
     */
    public Mono<RefreshToken> verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            return Mono.error(new RuntimeException("Токен недействителен или просрочен"));
        }
        return Mono.just(token);
    }
}