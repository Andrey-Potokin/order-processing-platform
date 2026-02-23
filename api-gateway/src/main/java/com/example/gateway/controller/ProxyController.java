package com.example.gateway.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProxyController {

    final WebClient webClient;

    @Value("${app.services.auth-url}")
    String authServiceUrl;

    public ProxyController(WebClient webClient) {
        this.webClient = webClient;
    }

    @PostMapping("/auth/login")
    public Mono<ResponseEntity<String>> login(ServerWebExchange exchange) {
        return proxyPost(exchange, authServiceUrl + "/api/auth/login");
    }

    @PostMapping("/auth/register")
    public Mono<ResponseEntity<String>> register(ServerWebExchange exchange) {
        return proxyPost(exchange, authServiceUrl + "/api/auth/register");
    }

    @PostMapping("/auth/refresh")
    public Mono<ResponseEntity<String>> refresh(ServerWebExchange exchange) {
        return proxyPost(exchange, authServiceUrl + "/api/auth/refresh");
    }

    private Mono<ResponseEntity<String>> proxyPost(ServerWebExchange exchange, String url) {
        return DataBufferUtils.join(exchange.getRequest().getBody())
            .map(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                DataBufferUtils.release(dataBuffer);
                return new String(bytes, StandardCharsets.UTF_8);
            })
            .defaultIfEmpty("")
            .flatMap(body -> webClient.post()
                .uri(URI.create(url))
                .headers(headers -> headers.addAll(exchange.getRequest().getHeaders()))
                .bodyValue(body)
                .retrieve()
                .toEntity(String.class));
    }
}