package hoanght.posapi.controller.admin;

import hoanght.posapi.dto.common.DataResponse;
import hoanght.posapi.dto.product.ProductCreationRequest;
import hoanght.posapi.dto.product.ProductResponse;
import hoanght.posapi.dto.product.ProductUpdateRequest;
import hoanght.posapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1/admin/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product management APIs")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Find all products", description = "Retrieve a paginated list of all products")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @Parameters({
            @Parameter(description = "Page number for pagination"),
            @Parameter(description = "Page size for pagination")
    })
    public ResponseEntity<DataResponse<PagedModel<ProductResponse>>> findAllProducts(@PageableDefault Pageable pageable) {
        Page<ProductResponse> products = productService.findAll(pageable);
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                products.getSize(),
                products.getNumber(),
                products.getTotalElements(),
                products.getTotalPages()
        );
        PagedModel<ProductResponse> pagedModel = PagedModel.of(products.getContent(), pageMetadata, linkTo(methodOn(ProductController.class).findAllProducts(pageable)).withSelfRel());
        return ResponseEntity.ok(DataResponse.success("Products retrieved successfully", pagedModel));
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Find product by ID", description = "Retrieve a product by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    public ResponseEntity<DataResponse<ProductResponse>> findProductById(@PathVariable Long productId) {
        ProductResponse product = productService.findProductById(productId);
        return ResponseEntity.ok(DataResponse.success("Product found successfully", product));
    }

    @PostMapping
    @Operation(summary = "Create product", description = "Create a new product with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    public ResponseEntity<DataResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductCreationRequest productCreationRequest) {
        ProductResponse createdProduct = productService.createProduct(productCreationRequest);
        return ResponseEntity.created(linkTo(methodOn(ProductController.class).findProductById(createdProduct.getId())).toUri())
                .body(DataResponse.success("Product created successfully", createdProduct));
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update product", description = "Update an existing product by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    public ResponseEntity<DataResponse<ProductResponse>> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductUpdateRequest productUpdateRequest) {
        ProductResponse updatedProduct = productService.updateProduct(productId, productUpdateRequest);
        return ResponseEntity.ok(DataResponse.success("Product updated successfully", updatedProduct));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete product", description = "Delete a product by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
