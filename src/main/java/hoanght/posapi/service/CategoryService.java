package hoanght.posapi.service;

import hoanght.posapi.dto.category.CategoryCreationRequest;
import hoanght.posapi.dto.category.CategoryResponse;
import hoanght.posapi.dto.category.CategoryUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CategoryService {
    Page<CategoryResponse> findAll(Pageable pageable);

    CategoryResponse findCategoryById(UUID categoryId);

    CategoryResponse createCategory(CategoryCreationRequest categoryCreationRequest);

    CategoryResponse updateCategory(UUID categoryId, CategoryUpdateRequest categoryUpdateRequest);

    void deleteCategory(UUID categoryId);
}
