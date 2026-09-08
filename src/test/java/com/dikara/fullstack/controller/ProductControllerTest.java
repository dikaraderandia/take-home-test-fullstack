package com.dikara.fullstack.controller;

import com.dikara.fullstack.dto.request.ProductRequest;
import com.dikara.fullstack.dto.response.ProductResponse;
import com.dikara.fullstack.exception.ResourceNotFoundException;
import com.dikara.fullstack.service.impl.ProductAsyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductAsyncService productAsyncService;

    private ProductController productController;

    @BeforeEach
    void setUp() {
        productController = new ProductController(productAsyncService);
    }

    private ProductResponse sampleProduct(Long id) {
        return ProductResponse.builder()
                .id(id)
                .name("Shirt")
                .description("Cotton shirt")
                .price(new BigDecimal("49.99"))
                .build();
    }

    // ---------- GET /api/products ----------

    @Test
    void getProducts_shouldDelegateToServiceAndReturnOk() {
        Page<ProductResponse> page =
                new PageImpl<>(List.of(sampleProduct(1L), sampleProduct(2L)));

        when(productAsyncService.getProductsAsync(isNull(), isNull(), isNull(), eq(0), eq(10)))
                .thenReturn(CompletableFuture.completedFuture(page));

        ResponseEntity<Page<ProductResponse>> response =
                productController.getProducts(null, null, null, 0, 10).join();

        verify(productAsyncService).getProductsAsync(null, null, null, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getContent().size());
    }

    @Test
    void getProducts_withFilters_shouldPassParametersToService() {
        Page<ProductResponse> page = new PageImpl<>(List.of(sampleProduct(1L)));

        when(productAsyncService.getProductsAsync(
                "shirt",
                new BigDecimal("10"),
                new BigDecimal("100"),
                1,
                5
        )).thenReturn(CompletableFuture.completedFuture(page));

        ResponseEntity<Page<ProductResponse>> response =
                productController.getProducts(
                        "shirt",
                        new BigDecimal("10"),
                        new BigDecimal("100"),
                        1,
                        5
                ).join();

        verify(productAsyncService).getProductsAsync(
                "shirt",
                new BigDecimal("10"),
                new BigDecimal("100"),
                1,
                5
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
    }

    // ---------- GET /api/products/{id} ----------

    @Test
    void getProductById_shouldReturnProduct() {
        ProductResponse product = sampleProduct(1L);
        when(productAsyncService.getByIdAsync(1L))
                .thenReturn(CompletableFuture.completedFuture(product));

        ResponseEntity<ProductResponse> response =
                productController.getProductById(1L).join();

        verify(productAsyncService).getByIdAsync(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Shirt", response.getBody().getName());
    }

    @Test
    void getProductById_whenNotFound_shouldPropagateException() {
        CompletableFuture<ProductResponse> failed = new CompletableFuture<>();
        failed.completeExceptionally(new ResourceNotFoundException("Product not found"));
        when(productAsyncService.getByIdAsync(999L)).thenReturn(failed);

        assertThrows(
                CompletionException.class,
                () -> productController.getProductById(999L).join()
        );
    }

    // ---------- POST /api/products ----------

    @Test
    void createProduct_shouldReturnCreatedStatusAndBody() {
        ProductRequest request = new ProductRequest();
        request.setName("Shirt");
        request.setPrice(new BigDecimal("49.99"));

        ProductResponse product = sampleProduct(3L);
        when(productAsyncService.createAsync(any()))
                .thenReturn(CompletableFuture.completedFuture(product));

        ResponseEntity<ProductResponse> response =
                productController.createProduct(request).join();

        verify(productAsyncService).createAsync(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(3L, response.getBody().getId());
        assertEquals("Shirt", response.getBody().getName());
    }

    // ---------- PUT /api/products/{id} ----------

    @Test
    void updateProduct_shouldReturnOkAndBody() {
        ProductRequest request = new ProductRequest();
        request.setName("Shirt Updated");
        request.setPrice(new BigDecimal("59.99"));

        ProductResponse updated = sampleProduct(1L);
        updated.setName("Shirt Updated");
        when(productAsyncService.updateAsync(1L, request))
                .thenReturn(CompletableFuture.completedFuture(updated));

        ResponseEntity<ProductResponse> response =
                productController.updateProduct(1L, request).join();

        verify(productAsyncService).updateAsync(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Shirt Updated", response.getBody().getName());
    }

    @Test
    void updateProduct_whenNotFound_shouldPropagateException() {
        ProductRequest request = new ProductRequest();
        request.setName("Shirt");
        request.setPrice(new BigDecimal("49.99"));

        CompletableFuture<ProductResponse> failed = new CompletableFuture<>();
        failed.completeExceptionally(new ResourceNotFoundException("Product not found"));
        when(productAsyncService.updateAsync(999L, request)).thenReturn(failed);

        assertThrows(
                CompletionException.class,
                () -> productController.updateProduct(999L, request).join()
        );
    }

    // ---------- DELETE /api/products/{id} ----------

    @Test
    void deleteProduct_shouldReturnNoContent() {
        when(productAsyncService.deleteAsync(1L))
                .thenReturn(CompletableFuture.completedFuture(null));

        ResponseEntity<Void> response = productController.deleteProduct(1L).join();

        verify(productAsyncService).deleteAsync(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteProduct_whenNotFound_shouldPropagateException() {
        CompletableFuture<Void> failed = new CompletableFuture<>();
        failed.completeExceptionally(new ResourceNotFoundException("Product not found"));
        when(productAsyncService.deleteAsync(999L)).thenReturn(failed);

        assertThrows(
                CompletionException.class,
                () -> productController.deleteProduct(999L).join()
        );
    }
}