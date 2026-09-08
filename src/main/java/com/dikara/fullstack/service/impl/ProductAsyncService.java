package com.dikara.fullstack.service.impl;

import com.dikara.fullstack.dto.request.ProductRequest;
import com.dikara.fullstack.dto.response.ProductResponse;
import com.dikara.fullstack.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductAsyncService {

    private final ProductService productService;

    @Async("taskExecutor")
    public CompletableFuture<Page<ProductResponse>> getProductsAsync(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int limit
    ) {
        log.info("Async: fetching products - page={}, limit={}", page, limit);
        return CompletableFuture.completedFuture(
                productService.getProducts(name, minPrice, maxPrice, page, limit)
        );
    }

    @Async("taskExecutor")
    public CompletableFuture<ProductResponse> getByIdAsync(Long id) {
        log.info("Async: fetching product by id={}", id);
        return CompletableFuture.completedFuture(productService.getById(id));
    }

    @Async("taskExecutor")
    public CompletableFuture<ProductResponse> createAsync(ProductRequest request) {
        log.info("Async: creating product name={}", request.getName());
        return CompletableFuture.completedFuture(productService.create(request));
    }

    @Async("taskExecutor")
    public CompletableFuture<ProductResponse> updateAsync(Long id, ProductRequest request) {
        log.info("Async: updating product id={}", id);
        return CompletableFuture.completedFuture(productService.update(id, request));
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> deleteAsync(Long id) {
        log.info("Async: deleting product id={}", id);
        productService.delete(id);
        return CompletableFuture.completedFuture(null);
    }
}