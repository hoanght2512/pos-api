package hoanght.posapi.service.impl;

import hoanght.posapi.controller.admin.ProductController;
import hoanght.posapi.dto.product.ProductCreationRequest;
import hoanght.posapi.dto.product.ProductResponse;
import hoanght.posapi.dto.product.ProductUpdateRequest;
import hoanght.posapi.exception.AlreadyExistsException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.model.Category;
import hoanght.posapi.model.Product;
import hoanght.posapi.repository.jpa.CategoryRepository;
import hoanght.posapi.repository.jpa.ProductRepository;
import hoanght.posapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = modelMapper.map(product, ProductResponse.class);
        response.add(linkTo(methodOn(ProductController.class).findProductById(product.getId())).withSelfRel());
        response.add(linkTo(methodOn(ProductController.class).updateProduct(product.getId(), null)).withRel("update"));
        response.add(linkTo(methodOn(ProductController.class).deleteProduct(product.getId())).withRel("delete"));
        return response;
    }

    @Override
    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public ProductResponse findProductById(Long productId) {
        ProductResponse response = productRepository.findById(productId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new NotFoundException("Product with ID " + productId + " not found"));
        response.add(linkTo(methodOn(ProductController.class).findAllProducts(Pageable.unpaged())).withRel("all"));
        return response;
    }

    @Override
    public ProductResponse createProduct(ProductCreationRequest productCreationRequest) {
        if (productRepository.existsByName(productCreationRequest.getName())) {
            throw new AlreadyExistsException("Product with name " + productCreationRequest.getName() + " already exists");
        }

        Category category = categoryRepository.findById(productCreationRequest.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category with ID " + productCreationRequest.getCategoryId() + " not found"));

        Product product = modelMapper.map(productCreationRequest, Product.class);
        product.setCategory(category);
        product = productRepository.save(product);
        return mapToResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest productUpdateRequest) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with ID " + productId + " not found"));

        if (productUpdateRequest.getName() != null) {
            if (productRepository.existsByName(productUpdateRequest.getName())) {
                throw new AlreadyExistsException("Product with name " + productUpdateRequest.getName() + " already exists");
            }
            existingProduct.setName(productUpdateRequest.getName());
        }

        if (productUpdateRequest.getCategoryId() != null) {
            existingProduct.setCategory(categoryRepository.findById(productUpdateRequest.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category with ID " + productUpdateRequest.getCategoryId() + " not found")));
        }

        Optional.ofNullable(productUpdateRequest.getDescription()).ifPresent(existingProduct::setDescription);
        Optional.ofNullable(productUpdateRequest.getSku()).ifPresent(existingProduct::setSku);
        Optional.ofNullable(productUpdateRequest.getPrice()).ifPresent(existingProduct::setPrice);
        Optional.ofNullable(productUpdateRequest.getImageUrl()).ifPresent(existingProduct::setImageUrl);

        existingProduct = productRepository.save(existingProduct);
        return mapToResponse(existingProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product with ID " + productId + " not found");
        }
        productRepository.deleteById(productId);
    }
}
