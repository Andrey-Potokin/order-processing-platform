package com.example.auth.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.core.GrantedAuthority;

/**
 * Перечисление ролей пользователей в системе.
 * <p>
 * Определяет уровни доступа и разрешённые действия для пользователей.
 * Реализует интерфейс {@link GrantedAuthority} для интеграции с Spring Security.
 * <p>
 * Поддерживаемые роли:
 * <ul>
 *   <li>{@link #USER} — обычный пользователь</li>
 *   <li>{@link #MANAGER} — менеджер, управляет заказами и товарами</li>
 *   <li>{@link #ADMIN} — администратор, имеет полный доступ к системе</li>
 * </ul>
 */
@Schema(description = "Роль пользователя в системе")
public enum UserRole implements GrantedAuthority {

    @Schema(description = "Обычный пользователь")
    USER,

    @Schema(description = "Менеджер магазина")
    MANAGER,

    @Schema(description = "Администратор системы")
    ADMIN;

    /**
     * Возвращает строковое представление роли, используемое Spring Security
     * для проверки доступа.
     * <p>
     * Например: "USER", "MANAGER", "ADMIN".
     *
     * @return название роли в виде строки
     */
    @Override
    public String getAuthority() {
        return name();
    }
}