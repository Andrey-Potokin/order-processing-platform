package com.example.product.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI (Swagger) для Product Service.
 *
 * <p>Настройка метаданных API, отображаемых в Swagger UI:
 * <ul>
 *   <li>Название: "Product Service API"</li>
 *   <li>Версия: "1.0.0"</li>
 *   <li>Описание: "API для управления каталогом товаров"</li>
 * </ul>
 *
 * <p>Доступно по адресу:
 * <a href="http://localhost:8085/swagger-ui.html">http://localhost:8085/swagger-ui.html</a>
 */
@Configuration
public class OpenApiConfig {

    /**
     * Определяет кастомную конфигурацию OpenAPI.
     *
     * @return объект {@link OpenAPI} с заданными метаданными
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Service API")
                        .version("1.0.0")
                        .description("API для управления каталогом товаров"));
    }
}