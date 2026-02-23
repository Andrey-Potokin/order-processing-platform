package com.example.auth.controller;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.UUID;

/**
 * REST-контроллер для предоставления публичных ключей в формате JWK Set (JSON Web Key Set).
 * <p>
 * Эндпоинт {@code /.well-known/jwks.json} используется OAuth 2.1 / OpenID Connect клиентами
 * и ресурс-серверами (например, {@code api-gateway}) для получения публичных ключей,
 * необходимых для проверки подписи JWT-токенов, выданных этим авторизационным сервером.
 * </p>
 * <p>
 * Контроллер использует асимметричную криптографию (RS256), где:
 * <ul>
 *   <li>Приватный ключ используется {@link com.example.auth.security.JwtUtil} для подписи токенов</li>
 *   <li>Публичный ключ предоставляется через этот эндпоинт для проверки подписи</li>
 * </ul>
 * </p>
 * <p>
 * Важно: метод {@link #getJwkSet()} возвращает чистый RFC 7517 JSON через {@code toJSONObject()},
 * чтобы гарантировать совместимость с Spring Security OAuth2.
 * </p>
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7517">RFC 7517 - JSON Web Key (JWK)</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc8414">RFC 8414 - OAuth 2.0 Authorization Server Metadata</a>
 */
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwkSetController {

    KeyPair keyPair;

    /**
     * Возвращает набор публичных ключей (JWK Set) в формате, соответствующем RFC 7517.
     * <p>
     * Эндпоинт доступен по стандартному пути:
     * {@code GET /.well-known/jwks.json}
     * </p>
     * <p>
     * Пример ответа:
     * </p>
     * <pre>
     * {
     *   "keys": [
     *     {
     *       "kty": "RSA",
     *       "alg": "RS256",
     *       "use": "sig",
     *       "kid": "5f8c2...",
     *       "n": "0vx7...",
     *       "e": "AQAB"
     *     }
     *   ]
     * }
     * </pre>
     *
     * @return карта в формате JSON, соответствующая RFC 7517, содержащая массив ключей
     * @see com.nimbusds.jose.jwk.JWKSet#toJSONObject()
     */
    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwkSet() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        JWK jwk = new RSAKey.Builder(publicKey)
                .keyID(UUID.randomUUID().toString())
                .keyUse(com.nimbusds.jose.jwk.KeyUse.SIGNATURE)
                .algorithm(com.nimbusds.jose.JWSAlgorithm.RS256)
                .build();

        return new JWKSet(jwk).toJSONObject();
    }
}