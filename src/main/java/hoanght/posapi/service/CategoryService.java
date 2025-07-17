package hoanght.posapi.service;

import hoanght.posapi.dto.category.CategoryCreationRequest;
import hoanght.posapi.dto.category.CategoryResponse;
import hoanght.posapi.dto.category.CategoryUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryResponse> findAll(Pageable pageable);

    CategoryResponse findCategoryById(Long categoryId);

    CategoryResponse createCategory(CategoryCreationRequest categoryCreationRequest);

    CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest categoryUpdateRequest);

    void deleteCategory(Long categoryId);
}
