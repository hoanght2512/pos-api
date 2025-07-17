package hoanght.posapi.service;

import hoanght.posapi.dto.product.ProductCreationRequest;
import hoanght.posapi.dto.product.ProductResponse;
import hoanght.posapi.dto.product.ProductUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponse> findAll(Pageable pageable);

    ProductResponse findProductById(Long productId);

    ProductResponse createProduct(ProductCreationRequest productCreationRequest);

    ProductResponse updateProduct(Long productId, ProductUpdateRequest productUpdateRequest);

    void deleteProduct(Long productId);
}
