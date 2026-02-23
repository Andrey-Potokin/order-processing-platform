package com.example.auth.util;

import com.example.auth.entity.UserRole;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Конвертеры для преобразования между Set<UserRole> и String[] при работе с PostgreSQL массивами.
 * Используется Spring Data R2DBC через R2dbcCustomConversions.
 */
@Component
public class RoleListConverter {

    /**
     * Преобразует Set<UserRole> в String[] для сохранения в колонку типа TEXT[].
     */
    @WritingConverter
    public static class SetToStringArrayConverter implements Converter<Set<UserRole>, String[]> {
        @Override
        public String[] convert(Set<UserRole> source) {
            if (source.isEmpty()) {
                return new String[0];
            }
            return source.stream()
                    .map(UserRole::name)
                    .toArray(String[]::new);
        }
    }

    /**
     * Преобразует String[] из колонки TEXT[] в Set<UserRole>.
     */
    @ReadingConverter
    public static class StringArrayToSetConverter implements Converter<String[], Set<UserRole>> {
        @Override
        public Set<UserRole> convert(String[] source) {
            if (source.length == 0) {
                return Set.of();
            }
            return Arrays.stream(source)
                    .map(UserRole::valueOf)
                    .collect(Collectors.toSet());
        }
    }
}