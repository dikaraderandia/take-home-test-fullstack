package com.dikara.fullstack.controller;

import com.dikara.fullstack.dto.request.ProductRequest;
import com.dikara.fullstack.dto.response.ProductResponse;
import com.dikara.fullstack.exception.ResourceNotFoundException;
import com.dikara.fullstack.service.ProductService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private ProductController productController;

    @BeforeEach
    void setUp() {
        productController = new ProductController(productService);
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

        when(productService.getProducts(isNull(), isNull(), isNull(), eq(0), eq(10)))
                .thenReturn(page);

        ResponseEntity<Page<ProductResponse>> response =
                productController.getProducts(null, null, null, 0, 10);

        verify(productService).getProducts(null, null, null, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getContent().size());
    }

    @Test
    void getProducts_withFilters_shouldPassParametersToService() {
        Page<ProductResponse> page = new PageImpl<>(List.of(sampleProduct(1L)));

        when(productService.getProducts("shirt", new BigDecimal("10"), new BigDecimal("100"), 1, 5))
                .thenReturn(page);

        ResponseEntity<Page<ProductResponse>> response =
                productController.getProducts(
                        "shirt",
                        new BigDecimal("10"),
                        new BigDecimal("100"),
                        1,
                        5
                );

        verify(productService).getProducts("shirt", new BigDecimal("10"), new BigDecimal("100"), 1, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
    }

    // ---------- GET /api/products/{id} ----------

    @Test
    void getProductById_shouldReturnProduct() {
        ProductResponse product = sampleProduct(1L);
        when(productService.getById(1L)).thenReturn(product);

        ResponseEntity<ProductResponse> response = productController.getProductById(1L);

        verify(productService).getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Shirt", response.getBody().getName());
    }

    @Test
    void getProductById_whenNotFound_shouldPropagateException() {
        when(productService.getById(999L))
                .thenThrow(new ResourceNotFoundException("Product not found"));

        assertThrows(
                ResourceNotFoundException.class,
                () -> productController.getProductById(999L)
        );
    }

    // ---------- POST /api/products ----------

    @Test
    void createProduct_shouldReturnCreatedStatusAndBody() {
        ProductRequest request = new ProductRequest();
        request.setName("Shirt");
        request.setPrice(new BigDecimal("49.99"));

        ProductResponse product = sampleProduct(3L);
        when(productService.create(any())).thenReturn(product);

        ResponseEntity<ProductResponse> response = productController.createProduct(request);

        verify(productService).create(request);

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
        when(productService.update(1L, request)).thenReturn(updated);

        ResponseEntity<ProductResponse> response = productController.updateProduct(1L, request);

        verify(productService).update(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Shirt Updated", response.getBody().getName());
    }

    @Test
    void updateProduct_whenNotFound_shouldPropagateException() {
        ProductRequest request = new ProductRequest();
        request.setName("Shirt");
        request.setPrice(new BigDecimal("49.99"));

        when(productService.update(999L, request))
                .thenThrow(new ResourceNotFoundException("Product not found"));

        assertThrows(
                ResourceNotFoundException.class,
                () -> productController.updateProduct(999L, request)
        );
    }

    // ---------- DELETE /api/products/{id} ----------

    @Test
    void deleteProduct_shouldReturnNoContent() {
        ResponseEntity<Void> response = productController.deleteProduct(1L);

        verify(productService).delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteProduct_whenNotFound_shouldPropagateException() {
        doThrow(new ResourceNotFoundException("Product not found"))
                .when(productService).delete(999L);

        assertThrows(
                ResourceNotFoundException.class,
                () -> productController.deleteProduct(999L)
        );
    }
}
