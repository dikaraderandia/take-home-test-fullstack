package com.dikara.fullstack.service.impl;

import com.dikara.fullstack.dto.request.ProductRequest;
import com.dikara.fullstack.dto.response.ProductResponse;
import com.dikara.fullstack.entity.Product;
import com.dikara.fullstack.exception.ResourceNotFoundException;
import com.dikara.fullstack.repository.ProductRepository;
import com.dikara.fullstack.service.ProductService;
import com.dikara.fullstack.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "productsList", key = "#name + ':' + #minPrice + ':' + #maxPrice + ':' + #page + ':' + #limit")
    public Page<ProductResponse> getProducts(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int limit
    ) {
        log.info("Fetching products - name={}, minPrice={}, maxPrice={}, page={}, limit={}",
                name, minPrice, maxPrice, page, limit);

        Pageable pageable = PageRequest.of(page, limit);

        Specification<Product> specification =
                ProductSpecification.search(name, minPrice, maxPrice);

        Page<ProductResponse> result = productRepository
                .findAll(specification, pageable)
                .map(this::toResponse);

        log.info("Found {} products", result.getTotalElements());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "productDetail", key = "#id")
    public ProductResponse getById(Long id) {
        log.info("Fetching product by id={}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id={}", id);
                    return new ResourceNotFoundException("Product not found");
                });

        return toResponse(product);
    }

    @Override
    @CacheEvict(cacheNames = {"productsList", "productDetail"}, allEntries = true)
    public ProductResponse create(ProductRequest request) {
        log.info("Creating new product name={}", request.getName());

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCreatedAt(new Date());

        Product saved = productRepository.save(product);
        log.info("Product created with id={}", saved.getId());

        return toResponse(saved);
    }

    @Override
    @CacheEvict(cacheNames = {"productsList", "productDetail"}, allEntries = true)
    public ProductResponse update(Long id, ProductRequest request) {
        log.info("Updating product id={}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id={}", id);
                    return new ResourceNotFoundException("Product not found");
                });

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        Product updated = productRepository.save(product);
        log.info("Product updated id={}", updated.getId());

        return toResponse(updated);
    }

    @Override
    @CacheEvict(cacheNames = {"productsList", "productDetail"}, allEntries = true)
    public void delete(Long id) {
        log.info("Deleting product id={}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id={}", id);
                    return new ResourceNotFoundException("Product not found");
                });

        productRepository.delete(product);
        log.info("Product deleted id={}", id);
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
