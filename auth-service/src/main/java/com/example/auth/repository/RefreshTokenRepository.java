package com.example.auth.repository;

import com.example.auth.entity.RefreshToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Реактивный репозиторий для управления сущностями {@link RefreshToken}.
 *
 * <p>Предоставляет реактивные методы для доступа и модификации данных токенов обновления
 * в хранилище с использованием Spring Data R2DBC. Интерфейс расширяет {@link ReactiveCrudRepository},
 * что даёт доступ к базовым операциям CRUD (создание, чтение, обновление, удаление) в асинхронной,
 * неблокирующей манере.</p>
 *
 * <p>Основное назначение — хранение и поиск refresh-токенов по их строковому значению,
 * используемому при обновлении access-токена в механизме аутентификации.</p>
 *
 * @see RefreshToken
 * @see ReactiveCrudRepository
 */
@Repository
public interface RefreshTokenRepository extends ReactiveCrudRepository<RefreshToken, Long> {

    /**
     * Находит и возвращает токен обновления по его строковому значению.
     *
     * <p>Выполняет асинхронный запрос к базе данных для поиска единственного {@link RefreshToken},
     * соответствующего переданному токену. Если такой токен найден — возвращается {@link Mono}
     * с объектом токена; если не найден — возвращается пустой {@link Mono}.</p>
     *
     * @param token строковое значение refresh-токена (не {@code null})
     * @return {@link Mono} содержащий найденный {@link RefreshToken}, или пустой {@link Mono}, если токен не найден
     */
    Mono<RefreshToken> findByToken(String token);
}