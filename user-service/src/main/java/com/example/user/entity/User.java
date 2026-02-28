package com.example.user.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Сущность пользователя, хранящаяся в таблице {@code users}.
 * <p>
 * Поддерживает оптимистичную блокировку через поле {@link #version},
 * что предотвращает потерю данных при параллельных обновлениях.
 * </p>
 */
@Table("users")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    Long id;

    String email;
    String role;

    @Version
    Integer version;
}