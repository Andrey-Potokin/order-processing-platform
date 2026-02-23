package com.example.auth.controller;

import com.example.auth.dto.JwtResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RefreshTokenRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.service.AuthService;
import com.example.auth.service.TokenService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@WebFluxTest(AuthController.class)
@Import(AuthControllerTest.TestConfig.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class AuthControllerTest {

    @Autowired
    WebTestClient webClient;

    @Autowired
    AuthService authService;

    @Autowired
    TokenService tokenService;

    JwtResponse jwtResponse;
    RegisterRequest registerRequest;
    LoginRequest loginRequest;
    RefreshTokenRequest refreshTokenRequest;

    @BeforeEach
    void setUp() {
        jwtResponse = new JwtResponse("access-token", "refresh-token");
        registerRequest = new RegisterRequest("test@example.com", "password123");
        loginRequest = new LoginRequest("test@example.com", "password123");
        refreshTokenRequest = new RefreshTokenRequest("refresh-token");

        reset(authService, tokenService);
    }

    @Test
    void register_ShouldReturnOk_WhenSuccess() {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(Mono.just(jwtResponse));

        webClient.post().uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponse.class)
                .isEqualTo(jwtResponse);
    }

    @Test
    void register_ShouldReturnConflict_WhenUserExists() {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("User already exists")));

        webClient.post().uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerRequest)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    void login_ShouldReturnOk_WhenCredentialsValid() {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(Mono.just(jwtResponse));

        webClient.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponse.class)
                .isEqualTo(jwtResponse);
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenCredentialsInvalid() {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("Invalid credentials")));

        webClient.post().uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void refreshToken_ShouldReturnOk_WhenTokenValid() {
        when(tokenService.refresh("refresh-token"))
                .thenReturn(Mono.just(jwtResponse));

        webClient.post().uri("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(refreshTokenRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtResponse.class)
                .isEqualTo(jwtResponse);
    }

    @Test
    void refreshToken_ShouldReturnUnauthorized_WhenTokenInvalid() {
        when(tokenService.refresh("invalid-token"))
                .thenReturn(Mono.empty());

        webClient.post().uri("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RefreshTokenRequest("invalid-token"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @org.springframework.context.annotation.Configuration
    @ComponentScan(basePackageClasses = AuthController.class)
    static class TestConfig {

        @Bean
        @Primary
        AuthService authService() {
            return mock(AuthService.class);
        }

        @Bean
        @Primary
        TokenService tokenService() {
            return mock(TokenService.class);
        }

        @Bean
        @Primary
        SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                    .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                    .authorizeExchange(auth -> auth
                            .pathMatchers("/api/auth/**").permitAll()
                            .anyExchange().authenticated()
                    )
                    .build();
        }
    }
}