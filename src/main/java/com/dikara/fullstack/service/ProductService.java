package com.dikara.fullstack.service;

import com.dikara.fullstack.dto.request.ProductRequest;
import com.dikara.fullstack.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface ProductService {

    Page<ProductResponse> getProducts(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int limit
    );

    ProductResponse getById(Long id);

    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);
}
