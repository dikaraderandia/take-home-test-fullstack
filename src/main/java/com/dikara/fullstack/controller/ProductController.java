package com.dikara.fullstack.controller;

import com.dikara.fullstack.dto.request.ProductRequest;
import com.dikara.fullstack.dto.response.ProductResponse;
import com.dikara.fullstack.service.impl.ProductAsyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductAsyncService productAsyncService;

    /**
     * GET /api/products
     *
     * Example:
     * /api/products
     * /api/products?name=shirt
     * /api/products?minPrice=10&maxPrice=100
     * /api/products?name=shirt&minPrice=10&maxPrice=100&page=0&limit=10
     */
    @GetMapping
    public CompletableFuture<ResponseEntity<Page<ProductResponse>>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit
    ) {
        return productAsyncService
                .getProductsAsync(name, minPrice, maxPrice, page, limit)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<ProductResponse>> getProductById(
            @PathVariable Long id
    ) {
        return productAsyncService
                .getByIdAsync(id)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * POST /api/products
     */
    @PostMapping
    public CompletableFuture<ResponseEntity<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request
    ) {
        return productAsyncService
                .createAsync(request)
                .thenApply(response ->
                        ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    /**
     * PUT /api/products/{id}
     */
    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        return productAsyncService
                .updateAsync(id, request)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteProduct(
            @PathVariable Long id
    ) {
        return productAsyncService
                .deleteAsync(id)
                .thenApply(response -> ResponseEntity.noContent().build());
    }
}