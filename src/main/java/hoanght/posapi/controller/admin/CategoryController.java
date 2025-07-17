package hoanght.posapi.controller.admin;

import hoanght.posapi.dto.category.CategoryCreationRequest;
import hoanght.posapi.dto.category.CategoryResponse;
import hoanght.posapi.dto.category.CategoryUpdateRequest;
import hoanght.posapi.dto.common.DataResponse;
import hoanght.posapi.service.CategoryService;
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
@RequiredArgsConstructor
@RequestMapping("/v1/admin/categories")
@Tag(name = "Category", description = "Category management APIs")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieve a paginated list of all categories")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @Parameters({
            @Parameter(description = "Page number for pagination"),
            @Parameter(description = "Page size for pagination")
    })
    public ResponseEntity<DataResponse<PagedModel<CategoryResponse>>> findAllCategories(@PageableDefault Pageable pageable) {
        Page<CategoryResponse> categories = categoryService.findAll(pageable);
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(categories.getSize(), categories.getNumber(), categories.getTotalElements(), categories.getTotalPages());
        PagedModel<CategoryResponse> pagedModel = PagedModel.of(categories.getContent(), pageMetadata, linkTo(methodOn(CategoryController.class).findAllCategories(pageable)).withSelfRel());
        return ResponseEntity.ok(DataResponse.success(pagedModel));
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Get category by ID", description = "Retrieve a category by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    public ResponseEntity<DataResponse<CategoryResponse>> findCategoryById(@PathVariable Long categoryId) {
        CategoryResponse category = categoryService.findCategoryById(categoryId);
        return ResponseEntity.ok(DataResponse.success("Category retrieved successfully", category));
    }

    @PostMapping
    @Operation(summary = "Create category", description = "Create a new category")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    public ResponseEntity<DataResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryCreationRequest categoryCreationRequest) {
        CategoryResponse createdCategory = categoryService.createCategory(categoryCreationRequest);
        return ResponseEntity.created(linkTo(methodOn(CategoryController.class).findCategoryById(createdCategory.getId())).toUri())
                .body(DataResponse.success("Category created successfully", createdCategory));
    }

    @PutMapping("/{categoryId}")
    @Operation(summary = "Update category", description = "Update an existing category by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    public ResponseEntity<DataResponse<CategoryResponse>> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryUpdateRequest categoryUpdateRequest) {
        CategoryResponse updatedCategory = categoryService.updateCategory(categoryId, categoryUpdateRequest);
        return ResponseEntity.ok(DataResponse.success("Category updated successfully", updatedCategory));
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Delete category", description = "Delete a category by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
