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
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    public ResponseEntity<DataResponse<PagedModel<EntityModel<CategoryResponse>>>> getAllCategories(Pageable pageable, PagedResourcesAssembler<CategoryResponse> assembler) {
        Page<CategoryResponse> categories = categoryService.findAll(pageable);
        PagedModel<EntityModel<CategoryResponse>> pagedModel = assembler.toModel(categories, category -> EntityModel.of(category,
                linkTo(methodOn(CategoryController.class).getCategoryById(category.getId())).withSelfRel(),
                linkTo(methodOn(CategoryController.class).updateCategory(category.getId(), null)).withRel("update"),
                linkTo(methodOn(CategoryController.class).deleteCategory(category.getId())).withRel("delete"),
                linkTo(methodOn(CategoryController.class).getAllCategories(pageable, null)).withRel("list-categories")
        ));
        pagedModel.add(linkTo(methodOn(CategoryController.class).createCategory(null)).withRel("create-category"));
        return ResponseEntity.ok(DataResponse.success(pagedModel));
    }

    @PostMapping
    @Operation(summary = "Create category", description = "Create a new category")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    public ResponseEntity<DataResponse<EntityModel<CategoryResponse>>> createCategory(@Valid @RequestBody CategoryCreationRequest categoryCreationRequest) {
        CategoryResponse createdCategory = categoryService.createCategory(categoryCreationRequest);
        EntityModel<CategoryResponse> entityModel = EntityModel.of(createdCategory,
                linkTo(methodOn(CategoryController.class).getCategoryById(createdCategory.getId())).withSelfRel(),
                linkTo(methodOn(CategoryController.class).updateCategory(createdCategory.getId(), null)).withRel("update"),
                linkTo(methodOn(CategoryController.class).deleteCategory(createdCategory.getId())).withRel("delete"),
                linkTo(methodOn(CategoryController.class).getAllCategories(Pageable.unpaged(), null)).withRel("list-categories")
        );
        return ResponseEntity.created(linkTo(methodOn(CategoryController.class).getCategoryById(createdCategory.getId())).toUri())
                .body(DataResponse.success("Category created successfully", entityModel));
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Get category by ID", description = "Retrieve a category by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    public ResponseEntity<DataResponse<EntityModel<CategoryResponse>>> getCategoryById(@PathVariable UUID categoryId) {
        CategoryResponse category = categoryService.findCategoryById(categoryId);
        EntityModel<CategoryResponse> entityModel = EntityModel.of(category,
                linkTo(methodOn(CategoryController.class).getCategoryById(categoryId)).withSelfRel(),
                linkTo(methodOn(CategoryController.class).updateCategory(categoryId, null)).withRel("update"),
                linkTo(methodOn(CategoryController.class).deleteCategory(categoryId)).withRel("delete"),
                linkTo(methodOn(CategoryController.class).getAllCategories(Pageable.unpaged(), null)).withRel("list-categories")
        );
        return ResponseEntity.ok(DataResponse.success(entityModel));
    }

    @PutMapping("/{categoryId}")
    @Operation(summary = "Update category", description = "Update an existing category by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    public ResponseEntity<DataResponse<EntityModel<CategoryResponse>>> updateCategory(@PathVariable UUID categoryId, @Valid @RequestBody CategoryUpdateRequest categoryUpdateRequest) {
        CategoryResponse updatedCategory = categoryService.updateCategory(categoryId, categoryUpdateRequest);
        EntityModel<CategoryResponse> entityModel = EntityModel.of(updatedCategory,
                linkTo(methodOn(CategoryController.class).getCategoryById(categoryId)).withSelfRel(),
                linkTo(methodOn(CategoryController.class).updateCategory(categoryId, null)).withRel("update"),
                linkTo(methodOn(CategoryController.class).deleteCategory(categoryId)).withRel("delete"),
                linkTo(methodOn(CategoryController.class).getAllCategories(Pageable.unpaged(), null)).withRel("list-categories")
        );
        return ResponseEntity.ok(DataResponse.success(entityModel));
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Delete category", description = "Delete a category by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    public ResponseEntity<DataResponse<Void>> deleteCategory(@PathVariable UUID categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
