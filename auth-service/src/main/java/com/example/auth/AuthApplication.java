package com.example.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

/**
 * Главный класс приложения, запускающий сервис аутентификации.
 *
 * <p>Аннотация {@link SpringBootApplication} включает автоматическую конфигурацию,
 * сканирование компонентов и определяет этот класс как точку входа приложения.</p>
 *
 * <p>Для управления миграциями базы данных используется Liquibase — его автонастройка
 * подключается явно через {@link ImportAutoConfiguration}, чтобы гарантировать корректную
 * инициализацию схемы БД при старте приложения.</p>
 *
 * @see SpringApplication
 * @see LiquibaseAutoConfiguration
 */
@SpringBootApplication
@ImportAutoConfiguration(LiquibaseAutoConfiguration.class)
public class AuthApplication {

    /**
     * Точка входа в приложение.
     *
     * <p>Метод запускает Spring Boot приложение, инициализируя контекст Spring
     * на основе конфигурации, указанной в классе {@link AuthApplication}.</p>
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}