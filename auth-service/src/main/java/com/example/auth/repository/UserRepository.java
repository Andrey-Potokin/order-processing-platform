package com.example.auth.repository;

import com.example.auth.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Реактивный репозиторий для выполнения операций с сущностью {@link User} в базе данных.
 *
 * <p>Интерфейс расширяет {@link ReactiveCrudRepository}, предоставляя реактивные, неблокирующие
 * методы для работы с пользователями. Все операции выполняются асинхронно и возвращают типы
 * Project Reactor — в частности, {@link Mono}, что позволяет интегрироваться в полносвязную
 * реактивную цепочку обработки запросов.</p>
 *
 * <p>Основное назначение репозитория — хранение, извлечение и управление данными пользователей,
 * включая аутентификацию и авторизацию через email-идентификатор.</p>
 *
 * @see User
 * @see ReactiveCrudRepository
 * @see Mono
 */
@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    /**
     * Находит и возвращает пользователя по его адресу электронной почты.
     *
     * <p>Выполняет асинхронный поиск единственного пользователя ({@link User}) на основе
     * указанного email. Если пользователь найден, результат будет представлен в виде
     * {@link Mono}, содержащего объект пользователя; если не найден — возвращается пустой {@link Mono}.</p>
     *
     * <p>Метод используется в процессе аутентификации, например, при входе пользователя
     * в систему или проверке уникальности email при регистрации.</p>
     *
     * @param email адрес электронной почты пользователя (не {@code null})
     * @return {@link Mono} с найденным объектом {@link User}, или пустой {@link Mono}, если пользователь не найден
     */
    Mono<User> findByEmail(String email);
}