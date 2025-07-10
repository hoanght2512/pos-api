package hoanght.posapi.service.impl;

import hoanght.posapi.dto.category.CategoryCreationRequest;
import hoanght.posapi.dto.category.CategoryResponse;
import hoanght.posapi.dto.category.CategoryUpdateRequest;
import hoanght.posapi.entity.Category;
import hoanght.posapi.exception.AlreadyExistsException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.repository.CategoryRepository;
import hoanght.posapi.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<CategoryResponse> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(category -> modelMapper.map(category, CategoryResponse.class));
    }

    @Override
    public CategoryResponse findCategoryById(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .orElseThrow(() -> new NotFoundException("Category with ID " + categoryId + " not found"));
    }

    @Override
    public CategoryResponse createCategory(CategoryCreationRequest categoryCreationRequest) {
        if (categoryRepository.existsByName(categoryCreationRequest.getName()))
            throw new AlreadyExistsException("Category with name " + categoryCreationRequest.getName() + " already exists");
        Category category = modelMapper.map(categoryCreationRequest, Category.class);
        category = categoryRepository.save(category);
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    public CategoryResponse updateCategory(UUID categoryId, CategoryUpdateRequest categoryUpdateRequest) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with ID " + categoryId + " not found"));

        Optional.ofNullable(categoryUpdateRequest.getName()).ifPresent(existingCategory::setName);
        Optional.ofNullable(categoryUpdateRequest.getDescription()).ifPresent(existingCategory::setDescription);
        existingCategory = categoryRepository.save(existingCategory);
        return modelMapper.map(existingCategory, CategoryResponse.class);
    }

    @Override
    public void deleteCategory(UUID categoryId) {
        if (!categoryRepository.existsById(categoryId))
            throw new NotFoundException("Category with ID " + categoryId + " not found");
        categoryRepository.deleteById(categoryId);
    }
}
