//package com.example.product.service;
//
//import com.example.product.dto.ProductDto;
//import com.example.product.entity.Product;
//import com.example.product.mapper.ProductMapper;
//import com.example.product.repository.ProductRepository;
//import com.example.product.service.ProductService;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
///**
// * Реализация {@link ProductService}.
// */
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class ProductServiceImpl implements ProductService {
//
//    ProductRepository productRepository;
//
//    @Override
//    @Transactional
//    public ProductDto create(ProductDto productDto) {
//        Product product = ProductMapper.toEntity(productDto);
//        product.setActive(true);
//        Product saved = productRepository.save(product);
//        return ProductMapper.toDto(saved);
//    }
//
//    @Override
//    @Transactional
//    public ProductDto update(String id, ProductDto productDto) {
//        Product existing = productRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + id));
//        ProductMapper.toEntity(productDto).copyTo(existing); // или вручную
//        existing.setId(id);
//        Product updated = productRepository.save(existing);
//        return ProductMapper.toDto(updated);
//    }
//
//    @Override
//    public Optional<ProductDto> findById(String id) {
//        return productRepository.findById(id)
//                .map(ProductMapper::toDto);
//    }
//
//    @Override
//    public Page<ProductDto> findAll(Pageable pageable) {
//        return productRepository.findAll(pageable)
//                .map(productMapper::toDto);
//    }
//
//    @Override
//    public Page<ProductDto> search(
//            String name,
//            String category,
//            Double minPrice,
//            Double maxPrice,
//            Boolean activeOnly,
//            Pageable pageable) {
//        Boolean isActive = activeOnly == null || activeOnly;
//        return productRepository.findByNameContainingIgnoreCaseAndCategoryAndPriceBetweenAndActive(
//                        name != null ? name : "",
//                        category,
//                        minPrice != null ? minPrice : 0.0,
//                        maxPrice != null ? maxPrice : Double.MAX_VALUE,
//                        isActive,
//                        pageable)
//                .map(productMapper::toDto);
//    }
//
//    @Override
//    @Transactional
//    public void deactivate(String id) {
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + id));
//        product.setActive(false);
//        productRepository.save(product);
//    }
//
//    @Override
//    @Transactional
//    public void activate(String id) {
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + id));
//        product.setActive(true);
//        productRepository.save(product);
//    }
//}