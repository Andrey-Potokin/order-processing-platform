package com.example.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * Конфигурационный класс для генерации асимметричной пары ключей (private и public),
 * используемой в механизмах безопасности приложения, таких как подпись и верификация JWT-токенов.
 *
 * <p>Класс предоставляет бин {@link KeyPair}, сгенерированный с использованием алгоритма RSA
 * с длиной ключа 2048 бит — это обеспечивает достаточный уровень криптографической стойкости
 * для подписи токенов в рамках OAuth2 и JWT.</p>
 *
 * <p>Сгенерированный приватный ключ используется для подписи выдаваемых токенов,
 * а публичный ключ может быть предоставлен клиентам или другим сервисам для проверки
 * подписи токена (например, через JWK-эндпоинт или конфигурацию).</p>
 *
 * @see #keyPair()
 */
@Configuration
public class KeyConfig {

    /**
     * Создаёт и возвращает бин типа {@link KeyPair}, содержащий приватный и публичный ключи RSA.
     *
     * <p>Ключи генерируются с использованием {@link KeyPairGenerator} по алгоритму "RSA"
     * с размером модуля 2048 бит, что является стандартом для подписи JWT в современных
     * приложениях безопасности.</p>
     *
     * <p>Этот бин используется, например, компонентами Spring Security для:
     * <ul>
     *   <li>Подписи JWT-токенов при их выдаче</li>
     *   <li>Настройки Authorization Server (например, в Spring Authorization Server)</li>
     * </ul>
     * </p>
     *
     * @return сгенерированная пара ключей RSA (2048 бит)
     * @throws Exception если произойдёт ошибка при инициализации или генерации ключей
     */
    @Bean
    public KeyPair keyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }
}