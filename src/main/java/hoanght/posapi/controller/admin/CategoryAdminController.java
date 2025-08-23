package hoanght.posapi.controller.admin;

import hoanght.posapi.assembler.CategoryAssembler;
import hoanght.posapi.dto.category.CategoryCreationRequest;
import hoanght.posapi.dto.category.CategoryResponse;
import hoanght.posapi.dto.category.CategoryUpdateRequest;
import hoanght.posapi.dto.common.DataResponse;
import hoanght.posapi.model.Category;
import hoanght.posapi.service.CategoryService;
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
@RequiredArgsConstructor
@RequestMapping("/v1/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;
    private final CategoryAssembler categoryAssembler;

    @GetMapping
    public ResponseEntity<DataResponse<?>> findAllCategories(@PageableDefault Pageable pageable, PagedResourcesAssembler<Category> pagedResourcesAssembler) {
        Page<Category> categoryPage = categoryService.findAll(pageable);
        PagedModel<CategoryResponse> response = pagedResourcesAssembler.toModel(categoryPage, categoryAssembler);
        return ResponseEntity.ok(DataResponse.success("Categories retrieved successfully", response));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<DataResponse<?>> findCategoryById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(DataResponse.success("Category retrieved successfully",
                categoryAssembler.toModel(categoryService.findById(categoryId))));
    }

    @PostMapping
    public ResponseEntity<Void> createCategory(@Valid @RequestBody CategoryCreationRequest categoryCreationRequest) {
        Category createdCategory = categoryService.create(categoryCreationRequest);
        return ResponseEntity.created(linkTo(methodOn(CategoryAdminController.class).findCategoryById(createdCategory.getId())).toUri()).build();
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<DataResponse<?>> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryUpdateRequest categoryUpdateRequest) {
        return ResponseEntity.ok(DataResponse.success("Category updated successfully",
                categoryAssembler.toModel(categoryService.update(categoryId, categoryUpdateRequest))));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.delete(categoryId);
        return ResponseEntity.noContent().build();
    }
}
