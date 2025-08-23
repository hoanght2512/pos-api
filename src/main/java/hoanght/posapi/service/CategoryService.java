package hoanght.posapi.service;

import hoanght.posapi.dto.category.CategoryCreationRequest;
import hoanght.posapi.dto.category.CategoryUpdateRequest;
import hoanght.posapi.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<Category> findAll(Pageable pageable);

    Category findById(Long categoryId);

    Category create(CategoryCreationRequest categoryCreationRequest);

    Category update(Long categoryId, CategoryUpdateRequest categoryUpdateRequest);

    void delete(Long categoryId);
}
