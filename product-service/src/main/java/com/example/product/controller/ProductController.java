package com.example.product.controller;

import com.example.product.dto.ProductDto;
import com.example.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * REST-контроллер для управления товарами.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Product API", description = "Операции с товарами: CRUD, поиск, фильтрация")
public class ProductController {

    ProductService productService;

    @PostMapping
    @Operation(summary = "Создать товар", description = "Создаёт новый товар",
            responses = @ApiResponse(responseCode = "201", description = "Товар создан",
                    content = @Content(schema = @Schema(implementation = ProductDto.class))))
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto productDto) {
        ProductDto created = productService.create(productDto);
        return ResponseEntity.created(URI.create("/api/products/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить товар", description = "Обновляет существующий товар по ID")
    public ResponseEntity<ProductDto> update(@PathVariable String id, @Valid @RequestBody ProductDto productDto) {
        ProductDto updated = productService.update(id, productDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по ID")
    public ResponseEntity<ProductDto> getById(@PathVariable String id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Получить все товары с пагинацией")
    public ResponseEntity<Page<ProductDto>> getAll(
            @Parameter(hidden = true)
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ProductDto> page = productService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск товаров по фильтрам")
    public ResponseEntity<Page<ProductDto>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "true") Boolean activeOnly,
            @Parameter(hidden = true) Pageable pageable) {
        Page<ProductDto> results = productService.search(name, category, minPrice, maxPrice, activeOnly, pageable);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Деактивировать товар", description = "Помечает товар как неактивный")
    public ResponseEntity<Void> deactivate(@PathVariable String id) {
        productService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Активировать товар")
    public ResponseEntity<Void> activate(@PathVariable String id) {
        productService.activate(id);
        return ResponseEntity.ok().build();
    }
}