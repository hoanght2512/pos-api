package hoanght.posapi.service.impl;

import hoanght.posapi.dto.category.CategoryCreationRequest;
import hoanght.posapi.dto.category.CategoryUpdateRequest;
import hoanght.posapi.exception.AlreadyExistsException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.model.Category;
import hoanght.posapi.repository.jpa.CategoryRepository;
import hoanght.posapi.service.CategoryService;
import hoanght.posapi.utils.StringUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public Category findById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category with ID " + categoryId + " not found"));
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Category create(CategoryCreationRequest categoryCreationRequest) {
        if (categoryRepository.existsByName(categoryCreationRequest.getName())) {
            throw new AlreadyExistsException("Category with name " + categoryCreationRequest.getName() + " already exists");
        }
        Category category = modelMapper.map(categoryCreationRequest, Category.class);
        category.setSlug(generateSlug(category.getName()));
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category update(Long categoryId, CategoryUpdateRequest categoryUpdateRequest) {
        Category existingCategory = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category with ID " + categoryId + " not found"));

        if (categoryUpdateRequest.getName() != null) {
            if (categoryRepository.existsByNameAndIdNot(categoryUpdateRequest.getName(), existingCategory.getId()) && !existingCategory.getName().equals(categoryUpdateRequest.getName())) {
                throw new AlreadyExistsException("Category with name " + categoryUpdateRequest.getName() + " already exists");
            }
            existingCategory.setName(categoryUpdateRequest.getName());
            existingCategory.setSlug(generateSlug(existingCategory.getName()));
        }
        Optional.ofNullable(categoryUpdateRequest.getImageUrl()).ifPresent(existingCategory::setImageUrl);
        return categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public void delete(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Category with ID " + categoryId + " not found");
        }
        if (categoryRepository.existsByIdAndProductsIsNotEmpty(categoryId))
            throw new AlreadyExistsException("Cannot delete category with associated products");
        categoryRepository.deleteById(categoryId);
    }

    private String generateSlug(String name) {
        int counter = 0;
        String baseSlug = StringUtils.toSlug(name);
        while (categoryRepository.existsBySlug(counter == 0 ? baseSlug : baseSlug + "-" + counter)) {
            counter++;
        }
        return counter == 0 ? baseSlug : baseSlug + "-" + counter;
    }
}
