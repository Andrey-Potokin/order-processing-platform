package com.example.auth.security;

import com.example.auth.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Конфигурационный класс для настройки безопасности в реактивном приложении с использованием Spring Security.
 *
 * <p>Данный класс обеспечивает:
 * <ul>
 *   <li>Настройку цепочки фильтров безопасности ({@link SecurityWebFilterChain})</li>
 *   <li>Поддержку JWT-аутентификации через {@code oauth2ResourceServer}</li>
 *   <li>Разрешение доступа к публичным эндпоинтам: регистрация, вход, рефреш токена, метрики и документация</li>
 *   <li>Отключение ненужных механизмов аутентификации: CSRF, HTTP Basic, Form Login</li>
 *   <li>Интеграцию с репозиторием пользователей через {@link ReactiveUserDetailsService}</li>
 *   <li>Шифрование паролей с помощью {@link BCryptPasswordEncoder}</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {

    UserRepository userRepository;

    /**
     * Настраивает цепочку фильтров безопасности для реактивного сервера.
     *
     * <p>Определяет правила авторизации:
     * <ul>
     *   <li>Разрешает публичный доступ к:</li>
     *   <ul>
     *     <li>{@code /api/auth/register} — регистрация нового пользователя</li>
     *     <li>{@code /api/auth/login} — аутентификация и получение токена</li>
     *     <li>{@code /api/auth/refresh} — обновление access токена</li>
     *     <li>{@code /.well-known/jwks.json} — публичный ключ для проверки JWT (RFC 7517)</li>
     *     <li>{@code /actuator/**} — эндпоинты мониторинга (health, metrics и др.)</li>
     *     <li>{@code /v3/api-docs/**} — OpenAPI спецификация (Swagger JSON)</li>
     *     <li>{@code /swagger-ui/**} — интерфейс Swagger UI</li>
     *   </ul>
     *   <li>Все остальные запросы требуют аутентификации</li>
     * </ul>
     * </p>
     *
     * <p>Также отключает:
     * <ul>
     *   <li>CSRF защиту — не требуется для stateless API</li>
     *   <li>Форм-логин — используется JWT, а не сессии</li>
     *   <li>HTTP Basic — не используется</li>
     * </ul>
     * </p>
     *
     * <p>Настраивает обработку JWT через {@code oauth2ResourceServer}, используя кастомный
     * {@link ReactiveAuthenticationManager}, реализованный как {@link UserDetailsRepositoryReactiveAuthenticationManager}.</p>
     *
     * @param http объект {@link ServerHttpSecurity}, используемый для настройки безопасности
     * @return готовая цепочка фильтров безопасности {@link SecurityWebFilterChain}
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .authorizeExchange(auth -> auth
                .pathMatchers(
                    "/api/auth/register",
                    "/api/auth/login",
                    "/api/auth/refresh"
                ).permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/.well-known/jwks.json").permitAll()
                .pathMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**"
                ).permitAll()
                .anyExchange().authenticated()
            )
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.authenticationManager(jwtAuthenticationManager()))
            );
        return http.build();
    }

    /**
     * Создаёт менеджер аутентификации на основе репозитория пользователей.
     *
     * <p>Использует {@link UserDetailsRepositoryReactiveAuthenticationManager} для реактивной загрузки
     * данных пользователя через {@link ReactiveUserDetailsService} и применяет {@link PasswordEncoder}
     * для проверки пароля при необходимости (например, при логине).</p>
     *
     * @return экземпляр {@link ReactiveAuthenticationManager}
     */
    @Bean
    public ReactiveAuthenticationManager jwtAuthenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authManager =
            new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
        authManager.setPasswordEncoder(passwordEncoder());
        return authManager;
    }

    /**
     * Предоставляет реактивный сервис для загрузки деталей пользователя по имени (email).
     *
     * <p>Интегрируется с {@link UserRepository#findByEmail(String)} для получения пользователя из БД.
     * Возвращаемое значение автоматически кастится к {@link org.springframework.security.core.userdetails.UserDetails}.</p>
     *
     * @return реализация {@link ReactiveUserDetailsService}, загружающая пользователя по email
     */
    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
            .cast(org.springframework.security.core.userdetails.UserDetails.class);
    }

    /**
     * Предоставляет энкодер паролей, основанный на алгоритме BCrypt.
     *
     * <p>BCrypt является криптографически стойким алгоритмом хеширования паролей,
     * рекомендованным для использования в Spring Security.</p>
     *
     * @return экземпляр {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}