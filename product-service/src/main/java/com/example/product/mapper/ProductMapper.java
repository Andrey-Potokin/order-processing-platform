package com.example.product.mapper;

import com.example.product.dto.ProductDto;
import com.example.product.entity.Product;
import lombok.experimental.UtilityClass;

/**
 * Утилита для преобразования между {@link Product} и {@link ProductDto}.
 */
@UtilityClass
public class ProductMapper {

    /**
     * Преобразует сущность в DTO.
     *
     * @param product сущность товара
     * @return DTO товара
     */
    public static ProductDto toDto(Product product) {
        if (product == null) return null;
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory());
        dto.setActive(product.isActive());
        return dto;
    }

    /**
     * Преобразует DTO в сущность.
     * Используется при создании/обновлении.
     *
     * @param dto DTO товара
     * @return сущность товара
     */
    public static Product toEntity(ProductDto dto) {
        if (dto == null) return null;
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setActive(dto.isActive());
        return product;
    }
}