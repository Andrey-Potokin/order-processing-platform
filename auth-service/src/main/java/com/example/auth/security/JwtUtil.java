package com.example.auth.security;

import com.example.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Утилита для генерации, парсинга и валидации JWT-токенов с использованием алгоритма RS256.
 * <p>
 * Класс использует асимметричную криптографию:
 * <ul>
 *   <li>Подпись токена осуществляется приватным ключом ({@link RSAPrivateKey})</li>
 *   <li>Проверка подписи — публичным ключом ({@link RSAPublicKey})</li>
 * </ul>
 * </p>
 * <p>
 * Выдаваемые токены содержат:
 * <ul>
 *   <li>{@code subject} — имя пользователя (username)</li>
 *   <li>{@code userId} — идентификатор пользователя</li>
 *   <li>{@code roles} — список ролей с префиксом {@code ROLE_}</li>
 *   <li>{@code iss} (issuer) — указывает на выдавший сервис: {@code http://localhost:8081}</li>
 *   <li>{@code exp}, {@code iat} — срок действия</li>
 * </ul>
 * </p>
 * <p>
 * Используется в {@link com.example.auth.service.TokenService} для генерации access-токенов
 * при регистрации и входе пользователя.
 * </p>
 *
 * @see com.example.auth.service.TokenService
 * @see com.example.auth.controller.JwkSetController
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7519">RFC 7519 - JSON Web Token (JWT)</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7518">RFC 7518 - JSON Web Algorithms (JWA)</a>
 */
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtUtil {

    RSAPrivateKey privateKey;
    RSAPublicKey publicKey;
    long expirationHours;

    /**
     * Конструктор для инъекции зависимости.
     *
     * @param keyPair пара RSA-ключей (приватный + публичный),предоставляемая бином {@link com.example.auth.security.KeyConfig}
     * @param expirationHours срок действия токена в часах, внедряется из конфигурации
     */
    public JwtUtil(KeyPair keyPair, @Value("${jwt.expiration-hours}") long expirationHours) {
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
        this.expirationHours = expirationHours;
    }

    /**
     * Генерирует JWT-токен для указанного пользователя.
     *
     * @param user объект пользователя, для которого генерируется токен
     * @return строка JWT, подписанная приватным ключом с использованием RS256
     * @throws IllegalArgumentException если пользователь null
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + Duration.ofHours(expirationHours).toMillis());

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("roles", user.getRoles().stream()
                        .map(role -> "ROLE_" + role.name())
                        .collect(Collectors.toList()))
                .issuedAt(now)
                .expiration(expiryDate)
                .issuer("http://localhost:8081")
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    /**
     * Извлекает имя пользователя (subject) из JWT.
     *
     * @param token строка JWT
     * @return username из поля {@code sub}, или пустую строку, если токен недействителен
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Извлекает произвольное поле из JWT с помощью переданного резолвера.
     *
     * @param token строка JWT
     * @param claimsResolver функция извлечения нужного поля из {@link Claims}
     * @return извлечённое значение
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Парсит и возвращает все claims из JWT после проверки подписи.
     *
     * @param token строка JWT
     * @return объект {@link Claims}, содержащий все поля токена
     * @throws io.jsonwebtoken.security.SecurityException если подпись недействительна
     * @throws io.jsonwebtoken.ExpiredJwtException если токен просрочен
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Проверяет, валиден ли JWT-токен.
     * <p>
     * Проверяются:
     * <ul>
     *   <li>Целостность подписи</li>
     *   <li>Срок действия (exp)</li>
     * </ul>
     * </p>
     *
     * @param token строка JWT
     * @return {@code true}, если токен валиден; {@code false} — если недействителен или просрочен
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}