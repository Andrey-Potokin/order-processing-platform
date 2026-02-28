package com.example.user.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    String jdbcUrl;

    @Value("${spring.datasource.username}")
    String username;

    @Value("${spring.datasource.password}")
    String password;

    @Value("${spring.datasource.driver-class-name}")
    String driverClassName;

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
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        return new HikariDataSource(config);
    }
}