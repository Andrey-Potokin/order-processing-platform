package com.example.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * DTO для передачи данных о товаре через REST API.
 */
@Data
@Schema(description = "Информация о товаре")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto {

    @Schema(description = "Уникальный идентификатор товара")
    Long id;

    @NotBlank(message = "Название товара обязательно")
    @Schema(description = "Название товара", requiredMode = Schema.RequiredMode.REQUIRED, example = "Смартфон XYZ")
    String name;

    @NotNull(message = "Цена обязательна")
    @Positive(message = "Цена должна быть положительной")
    @Schema(description = "Цена товара", requiredMode = Schema.RequiredMode.REQUIRED, example = "299.99")
    Double price;

    @NotBlank(message = "Категория обязательна")
    @Schema(description = "Категория товара", requiredMode = Schema.RequiredMode.REQUIRED, example = "Электроника")
    String category;

    @Schema(description = "Статус активности товара", example = "true")
    boolean active;
}