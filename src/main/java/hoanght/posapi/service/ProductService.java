package hoanght.posapi.service;

import hoanght.posapi.dto.product.ProductCreationRequest;
import hoanght.posapi.dto.product.ProductUpdateRequest;
import hoanght.posapi.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<Product> findAllProducts(Pageable pageable);

    Product findProductById(Long productId);

    Product createProduct(ProductCreationRequest productCreationRequest);

    Product updateProduct(Long productId, ProductUpdateRequest productUpdateRequest);

    void deleteProduct(Long productId);
}
