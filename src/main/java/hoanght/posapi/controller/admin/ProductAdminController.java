package hoanght.posapi.controller.admin;

import hoanght.posapi.assembler.ProductAssembler;
import hoanght.posapi.dto.common.DataResponse;
import hoanght.posapi.dto.product.ProductCreationRequest;
import hoanght.posapi.dto.product.ProductResponse;
import hoanght.posapi.dto.product.ProductUpdateRequest;
import hoanght.posapi.model.Product;
import hoanght.posapi.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {
    private final ProductService productService;
    private final ProductAssembler productAssembler;

    @GetMapping
    public ResponseEntity<DataResponse<?>> findAllProducts(@PageableDefault Pageable pageable, PagedResourcesAssembler<Product> pagedResourcesAssembler) {
        Page<Product> products = productService.findAllProducts(pageable);
        PagedModel<ProductResponse> response = pagedResourcesAssembler.toModel(products, productAssembler);
        return ResponseEntity.ok(DataResponse.success("Products retrieved successfully", response));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<DataResponse<?>> findProductById(@PathVariable Long productId) {
        Product product = productService.findProductById(productId);
        ProductResponse response = productAssembler.toModel(product);
        return ResponseEntity.ok(DataResponse.success("Product found successfully", response));
    }

    @PostMapping
    public ResponseEntity<Void> createProduct(@Valid @RequestBody ProductCreationRequest productCreationRequest) {
        Product createdProduct = productService.createProduct(productCreationRequest);
        return ResponseEntity.created(linkTo(methodOn(ProductAdminController.class).findProductById(createdProduct.getId())).toUri()).build();
    }

    @PutMapping("/{productId}")
    public ResponseEntity<DataResponse<?>> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductUpdateRequest productUpdateRequest) {
        Product product = productService.updateProduct(productId, productUpdateRequest);
        ProductResponse response = productAssembler.toModel(product);
        return ResponseEntity.ok(DataResponse.success("Product updated successfully", response));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
