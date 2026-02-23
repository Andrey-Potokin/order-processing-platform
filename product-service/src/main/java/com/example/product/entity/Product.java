package com.example.product.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Сущность товара, хранится в MongoDB.
 */
@Document(collection = "products")
@Getter @Setter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {

    @Id
    Long id;

    @NonNull
    String name;

    @NonNull
    Double price;

    @NonNull
    String category;

    boolean active;
}