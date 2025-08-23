package hoanght.posapi.service.impl;

import hoanght.posapi.dto.product.ProductCreationRequest;
import hoanght.posapi.dto.product.ProductUpdateRequest;
import hoanght.posapi.exception.AlreadyExistsException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.model.Category;
import hoanght.posapi.model.Inventory;
import hoanght.posapi.model.Product;
import hoanght.posapi.repository.jpa.CategoryRepository;
import hoanght.posapi.repository.jpa.ProductRepository;
import hoanght.posapi.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    @Cacheable("products")
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Cacheable(value = "product", key = "#productId")
    public Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with ID " + productId + " not found"));
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product create(ProductCreationRequest productCreationRequest) {
        if (productRepository.existsByName(productCreationRequest.getName())) {
            throw new AlreadyExistsException("Product with name " + productCreationRequest.getName() + " already exists");
        }

        if (productCreationRequest.getSku() != null && productRepository.existsBySku(productCreationRequest.getSku())) {
            throw new AlreadyExistsException("Product with SKU " + productCreationRequest.getSku() + " already exists");
        }


        Category category = categoryRepository.findById(productCreationRequest.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category with ID " + productCreationRequest.getCategoryId() + " not found"));

        Product product = modelMapper.map(productCreationRequest, Product.class);
        product.setCategory(category);

        if (productCreationRequest.getCountable()) {
            Inventory newInventory = new Inventory();
            newInventory.setProduct(product);
            newInventory.setQuantity(productCreationRequest.getQuantity() != null ? productCreationRequest.getQuantity() : 0L);
            product.setInventory(newInventory);
        }

        return productRepository.save(product);
    }

    @Override
    @Transactional
    @CachePut(value = "product", key = "#productId")
    @CacheEvict(value = "products", allEntries = true)
    public Product update(Long productId, ProductUpdateRequest productUpdateRequest) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with ID " + productId + " not found"));

        if (productUpdateRequest.getName() != null) {
            if (!productUpdateRequest.getName().equalsIgnoreCase(existingProduct.getName())) {
                if (productRepository.existsByName(productUpdateRequest.getName())) {
                    throw new AlreadyExistsException("Product with name " + productUpdateRequest.getName() + " already exists");
                }
            }
            existingProduct.setName(productUpdateRequest.getName());
        }

        if (productUpdateRequest.getSku() != null) {
            if (!productUpdateRequest.getSku().equalsIgnoreCase(existingProduct.getSku())) {
                if (productRepository.existsBySku(productUpdateRequest.getSku())) {
                    throw new AlreadyExistsException("Product with SKU " + productUpdateRequest.getSku() + " already exists");
                }
            }
            existingProduct.setSku(productUpdateRequest.getSku());
        }

        if (productUpdateRequest.getCategoryId() != null) {
            existingProduct.setCategory(categoryRepository.findById(productUpdateRequest.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category with ID " + productUpdateRequest.getCategoryId() + " not found")));
        }

        Optional.ofNullable(productUpdateRequest.getDescription()).ifPresent(existingProduct::setDescription);
        Optional.ofNullable(productUpdateRequest.getPrice()).ifPresent(existingProduct::setPrice);
        Optional.ofNullable(productUpdateRequest.getImageUrl()).ifPresent(existingProduct::setImageUrl);

        if (productUpdateRequest.getCountable() != null) {
            existingProduct.setCountable(productUpdateRequest.getCountable());

            if (productUpdateRequest.getCountable()) {
                Inventory inventory = existingProduct.getInventory();
                if (inventory == null) {
                    inventory = new Inventory();
                    inventory.setProduct(existingProduct);
                    existingProduct.setInventory(inventory);
                }
                if (productUpdateRequest.getQuantity() != null) {
                    inventory.setQuantity(productUpdateRequest.getQuantity());
                }
            } else {
                if (existingProduct.getInventory() != null) {
                    existingProduct.setInventory(null);
                }
            }
        } else {
            if (existingProduct.getCountable() && productUpdateRequest.getQuantity() != null) {
                Inventory inventory = existingProduct.getInventory();
                if (inventory == null) {
                    inventory = new Inventory();
                    inventory.setProduct(existingProduct);
                    existingProduct.setInventory(inventory);
                }
                inventory.setQuantity(productUpdateRequest.getQuantity());
            }
        }

        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#productId"),
            @CacheEvict(value = "products", allEntries = true)
    })
    public void delete(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product with ID " + productId + " not found");
        }
        productRepository.deleteById(productId);
    }
}