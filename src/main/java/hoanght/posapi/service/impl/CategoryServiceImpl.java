package hoanght.posapi.service.impl;

import hoanght.posapi.dto.category.CategoryCreationRequest;
import hoanght.posapi.dto.category.CategoryUpdateRequest;
import hoanght.posapi.exception.AlreadyExistsException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.model.Category;
import hoanght.posapi.repository.jpa.CategoryRepository;
import hoanght.posapi.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Cacheable(value = "category", key = "#categoryId")
    public Category findById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with ID " + categoryId + " not found"));
    }

    @Override
    @Cacheable(value = "categories")
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public Category create(CategoryCreationRequest categoryCreationRequest) {
        if (categoryRepository.existsByName(categoryCreationRequest.getName())) {
            throw new AlreadyExistsException("Category with name " + categoryCreationRequest.getName() + " already exists");
        }
        Category category = modelMapper.map(categoryCreationRequest, Category.class);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(value = "category", key = "#categoryId")
            },
            evict = {
                    @CacheEvict(value = "categories", allEntries = true),
                    @CacheEvict(value = "products", allEntries = true),
                    @CacheEvict(value = "product", allEntries = true)
            }
    )
    public Category update(Long categoryId, CategoryUpdateRequest categoryUpdateRequest) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with ID " + categoryId + " not found"));

        if (categoryUpdateRequest.getName() != null) {
            if (categoryRepository.existsByName(categoryUpdateRequest.getName()) && !existingCategory.getName().equals(categoryUpdateRequest.getName())) {
                throw new AlreadyExistsException("Category with name " + categoryUpdateRequest.getName() + " already exists");
            }
            existingCategory.setName(categoryUpdateRequest.getName());
        }

        Optional.ofNullable(categoryUpdateRequest.getDescription()).ifPresent(existingCategory::setDescription);
        Optional.ofNullable(categoryUpdateRequest.getImageUrl()).ifPresent(existingCategory::setImageUrl);
        return categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "category", key = "#categoryId"),
            @CacheEvict(value = "categories", allEntries = true),
            @CacheEvict(value = "products", allEntries = true),
            @CacheEvict(value = "product", allEntries = true)
    })
    public void delete(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Category with ID " + categoryId + " not found");
        }
        categoryRepository.deleteById(categoryId);
    }
}
