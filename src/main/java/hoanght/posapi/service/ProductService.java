package hoanght.posapi.service;

import hoanght.posapi.dto.product.ProductCreationRequest;
import hoanght.posapi.dto.product.ProductUpdateRequest;
import hoanght.posapi.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<Product> findAll(Pageable pageable);

    Product findById(Long productId);

    Product create(ProductCreationRequest productCreationRequest);

    Product update(Long productId, ProductUpdateRequest productUpdateRequest);

    void delete(Long productId);
}
