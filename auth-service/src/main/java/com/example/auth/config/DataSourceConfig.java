package com.example.auth.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Конфигурационный класс для настройки источника данных (DataSource) приложения.
 *
 * <p>Данный класс определяет бин {@link DataSource}, используя пул соединений HikariCP
 * для эффективного управления подключениями к базе данных PostgreSQL.</p>
 *
 * <p>Конфигурация включает:
 * <ul>
 *   <li>URL подключения к базе данных: {@code jdbc:postgresql://localhost:5432/authdb}</li>
 *   <li>Имя пользователя: {@code auth_user}</li>
 *   <li>Пароль: {@code auth_pass}</li>
 *   <li>Драйвер JDBC: {@code org.postgresql.Driver}</li>
 * </ul>
 * </p>
 *
 * <p>Несмотря на то, что приложение является реактивным, данный DataSource необходим
 * для интеграции с Liquibase — инструментом управления миграциями базы данных,
 * который требует блокирующего JDBC-драйвера для выполнения SQL-скриптов.
 * Таким образом, этот класс обеспечивает совместимость реактивной архитектуры
 * с традиционными инструментами миграции, позволяя запускать изменения схемы БД
 * при старте приложения.</p>
 *
 * @see DataSource
 * @see HikariDataSource
 */
@Configuration
public class DataSourceConfig {

    /**
     * Создаёт и возвращает основной бин источника данных с использованием HikariCP.
     *
     * <p>Этот метод настраивает пул соединений с базой данных с помощью {@link HikariConfig}
     * и возвращает экземпляр {@link HikariDataSource}. Бин помечен аннотацией {@link Primary},
     * чтобы указать его как предпочтительный источник данных в случае наличия нескольких бинов типа {@link DataSource}.</p>
     *
     * <p>DataSource используется Liquibase для выполнения миграций базы данных
     * в реактивном окружении, где нативно используется R2DBC. Поскольку Liquibase
     * работает только с JDBC, этот бин предоставляет необходимое JDBC-подключение.</p>
     *
     * @return настроенный экземпляр {@link DataSource}, используемый в том числе Liquibase
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/authdb");
        config.setUsername("auth_user");
        config.setPassword("auth_pass");
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }
}