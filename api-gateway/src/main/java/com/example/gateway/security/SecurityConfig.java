package com.example.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Конфигурация безопасности для API Gateway.
 * <p>
 * Использует {@link NimbusJwtDecoder} для прямой загрузки JWK Set из
 * {@code http://localhost:8081/.well-known/jwks.json}, обёрнутый в реактивный декодер
 * через лямбду, без использования устаревших классов.
 * </p>
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Основная цепочка фильтров безопасности.
     * <p>
     * Разрешает доступ:
     * <ul>
     *   <li>К Actuator — без токена</li>
     *   <li>Все остальные запросы — только с валидным JWT</li>
     * </ul>
     * </p>
     *
     * @param http объект настройки HTTP-безопасности
     * @return настроенная цепочка фильтров
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/api/auth/register", "/api/auth/login", "/api/auth/refresh").permitAll()
                .anyExchange().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtDecoder(reactiveJwtDecoder())))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable);

        return http.build();
    }

    /**
     * Создаёт реактивный декодер JWT через лямбду, использующую Nimbus.
     * <p>
     * Прямая загрузка JWK Set: {@code http://localhost:8081/.well-known/jwks.json}
     * Не требует OpenID Connect Discovery.
     * </p>
     *
     * @return реактивный декодер, работающий напрямую с JWKS
     */
    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        JwtDecoder delegate = NimbusJwtDecoder.withJwkSetUri("http://localhost:8081/.well-known/jwks.json").build();

        return token -> Mono.fromCallable(() -> delegate.decode(token));
    }
}