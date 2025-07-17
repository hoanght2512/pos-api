package hoanght.posapi.service.impl;

import hoanght.posapi.controller.admin.CategoryController;
import hoanght.posapi.dto.category.CategoryCreationRequest;
import hoanght.posapi.dto.category.CategoryResponse;
import hoanght.posapi.dto.category.CategoryUpdateRequest;
import hoanght.posapi.exception.AlreadyExistsException;
import hoanght.posapi.exception.BadRequestException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.model.Category;
import hoanght.posapi.repository.jpa.CategoryRepository;
import hoanght.posapi.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = modelMapper.map(category, CategoryResponse.class);
        response.add(linkTo(methodOn(CategoryController.class).findCategoryById(category.getId())).withSelfRel());
        response.add(linkTo(methodOn(CategoryController.class).updateCategory(category.getId(), null)).withRel("update"));
        response.add(linkTo(methodOn(CategoryController.class).deleteCategory(category.getId())).withRel("delete"));
        return response;
    }

    @Override
    public Page<CategoryResponse> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public CategoryResponse findCategoryById(Long categoryId) {
        CategoryResponse response = categoryRepository.findById(categoryId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new NotFoundException("Category with ID " + categoryId + " not found"));
        response.add(linkTo(methodOn(CategoryController.class).findAllCategories(Pageable.unpaged())).withRel("all"));
        return response;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreationRequest categoryCreationRequest) {
        if (categoryRepository.existsByName(categoryCreationRequest.getName())) {
            throw new AlreadyExistsException("Category with name " + categoryCreationRequest.getName() + " already exists");
        }
        Category category = modelMapper.map(categoryCreationRequest, Category.class);
        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest categoryUpdateRequest) {
        Category existingCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category with ID " + categoryId + " not found"));

        if (categoryUpdateRequest.getName() != null) {
            if (categoryRepository.existsByName(categoryUpdateRequest.getName()) && !existingCategory.getName().equals(categoryUpdateRequest.getName())) {
                throw new AlreadyExistsException("Category with name " + categoryUpdateRequest.getName() + " already exists");
            }
            existingCategory.setName(categoryUpdateRequest.getName());
        }

        Optional.ofNullable(categoryUpdateRequest.getDescription()).ifPresent(existingCategory::setDescription);
        Optional.ofNullable(categoryUpdateRequest.getImageUrl()).ifPresent(existingCategory::setImageUrl);
        existingCategory = categoryRepository.save(existingCategory);
        return mapToResponse(existingCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Category with ID " + categoryId + " not found");
        }
        if (categoryRepository.existsByIdAndProductsIsNotEmpty(categoryId)) {
            throw new BadRequestException("Cannot delete category with ID " + categoryId + " because it has associated products");
        }
        categoryRepository.deleteById(categoryId);
    }
}
