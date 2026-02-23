package com.example.product.service;

import com.example.product.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Сервис для управления товарами.
 */
public interface ProductService {

    /**
     * Создаёт новый товар.
     *
     * @param productDto данные нового товара
     * @return сохранённый товар
     */
    ProductDto create(ProductDto productDto);

    /**
     * Обновляет существующий товар по ID.
     *
     * @param id         идентификатор товара
     * @param productDto новые данные
     * @return обновлённый товар
     */
    ProductDto update(String id, ProductDto productDto);

    /**
     * Находит товар по ID.
     *
     * @param id идентификатор
     * @return Optional с товаром
     */
    Optional<ProductDto> findById(String id);

    /**
     * Возвращает все товары с пагинацией.
     *
     * @param pageable параметры пагинации
     * @return страница товаров
     */
    Page<ProductDto> findAll(Pageable pageable);

    /**
     * Поиск товаров по фильтрам.
     *
     * @param name       часть названия (опционально)
     * @param category   категория (опционально)
     * @param minPrice   минимальная цена (опционально)
     * @param maxPrice   максимальная цена (опционально)
     * @param activeOnly только активные (если true)
     * @param pageable   параметры пагинации
     * @return страница товаров
     */
    Page<ProductDto> search(
            String name,
            String category,
            Double minPrice,
            Double maxPrice,
            Boolean activeOnly,
            Pageable pageable
    );

    /**
     * Помечает товар как удалённый (неактивный).
     *
     * @param id идентификатор товара
     */
    void deactivate(String id);

    /**
     * Активирует товар.
     *
     * @param id идентификатор товара
     */
    void activate(String id);
}