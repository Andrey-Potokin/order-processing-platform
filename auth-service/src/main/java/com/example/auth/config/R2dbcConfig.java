package com.example.auth.config;

import com.example.auth.util.RoleListConverter;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;

import java.util.Arrays;

/**
 * Конфигурационный класс для настройки пользовательских преобразований (конвертеров) в R2DBC.
 *
 * <p>Этот класс определяет бин {@link R2dbcCustomConversions}, который регистрирует
 * пользовательские конвертеры для работы с типами данных, не поддерживаемыми напрямую
 * Spring Data R2DBC. В частности, реализовано преобразование между множеством ролей
 * ({@code Set<UserRole>}) и массивом строк в базе данных.</p>
 *
 * <p>Используется в реактивном контексте приложения для корректной маршалинг/демаршалинг
 * пользовательских типов данных при взаимодействии с PostgreSQL через R2DBC.</p>
 *
 * @see R2dbcCustomConversions
 * @see RoleListConverter
 */
@Configuration
public class R2dbcConfig {

    /**
     * Создаёт и возвращает экземпляр {@link R2dbcCustomConversions} с зарегистрированными
     * пользовательскими конвертерами.
     *
     * <p>На основе диалекта базы данных, определённого через {@link DialectResolver},
     * создаётся набор кастомных преобразований, включающий:</p>
     * <ul>
     *   <li>{@link RoleListConverter.SetToStringArrayConverter} — преобразует {@code Set<UserRole>} в массив строк для сохранения в БД</li>
     *   <li>{@link RoleListConverter.StringArrayToSetConverter} — преобразует массив строк из БД обратно в {@code Set<UserRole>}</li>
     * </ul>
     *
     * <p>Это позволяет хранить коллекцию ролей пользователя как массив текстовых значений
     * в PostgreSQL (например, {@code VARCHAR[]}), обеспечивая удобную работу с типизированными
     * объектами на уровне приложения.</p>
     *
     * @param connectionFactory фабрика соединений R2DBC, используемая для определения диалекта БД
     * @return настроенный экземпляр {@link R2dbcCustomConversions}
     */
    @Bean
    public R2dbcCustomConversions r2dbcCustomConversions(ConnectionFactory connectionFactory) {
        var dialect = DialectResolver.getDialect(connectionFactory);
        return R2dbcCustomConversions.of(dialect,
                Arrays.asList(
                        new RoleListConverter.SetToStringArrayConverter(),
                        new RoleListConverter.StringArrayToSetConverter()
                )
        );
    }
}