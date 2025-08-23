package hoanght.posapi.service;

import hoanght.posapi.dto.category.CategoryCreationRequest;
import hoanght.posapi.dto.category.CategoryResponse;
import hoanght.posapi.dto.category.CategoryUpdateRequest;
import hoanght.posapi.exception.AlreadyExistsException;
import hoanght.posapi.exception.BadRequestException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.model.Category;
import hoanght.posapi.repository.jpa.CategoryRepository;
import hoanght.posapi.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Category Service Tests")
@Tag("Service")
public class CategoryServiceTests {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;
    private CategoryResponse testCategoryResponse;
    private CategoryCreationRequest testCreationRequest;
    private CategoryUpdateRequest testUpdateRequest;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Đồ uống");
        testCategory.setDescription("Các loại đồ uống");
        testCategory.setImageUrl("https://example.com/drink.jpg");

        testCategoryResponse = new CategoryResponse();
        testCategoryResponse.setId(1L);
        testCategoryResponse.setName("Đồ uống");
        testCategoryResponse.setDescription("Các loại đồ uống");
        testCategoryResponse.setImageUrl("https://example.com/drink.jpg");

        testCreationRequest = new CategoryCreationRequest();
        testCreationRequest.setName("Món ăn");
        testCreationRequest.setDescription("Các món ăn chính");
        testCreationRequest.setImageUrl("https://example.com/food.jpg");

        testUpdateRequest = new CategoryUpdateRequest();
        testUpdateRequest.setName("Đồ uống Cập Nhật");
        testUpdateRequest.setDescription("Đồ uống các loại đã cập nhật");
    }

    @Test
    @DisplayName("Find All Categories - Success")
    void findAll_ShouldReturnPagedCategoryResponses() {
        List<Category> categories = Collections.singletonList(testCategory);
        Page<Category> categoryPage = new PageImpl<>(categories, PageRequest.of(0, 10), categories.size());
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categoryPage);

        Page<Category> result = categoryService.findAll(PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testCategoryResponse.getName(), result.getContent().get(0).getName());

        verify(categoryRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Find Category By ID - Success")
    void findCategoryById_ShouldReturnResponse() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));

        Category result = categoryService.findById(1L);

        assertNotNull(result);
        assertEquals(testCategoryResponse.getName(), result.getName());

        verify(categoryRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Find Category By ID - Not Found")
    void findById_ShouldThrowNotFoundException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.findById(99L));

        verify(categoryRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Create Category - Success")
    void createCategory_ShouldReturnCreatedResponse() {
        Category mappedCategory = new Category();
        mappedCategory.setName(testCreationRequest.getName());
        mappedCategory.setDescription(testCreationRequest.getDescription());
        mappedCategory.setImageUrl(testCreationRequest.getImageUrl());

        when(modelMapper.map(testCreationRequest, Category.class)).thenReturn(mappedCategory);
        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(mappedCategory)).thenReturn(mappedCategory);
    }

    @Test
    @DisplayName("Create Category - Name Already Exists")
    void create_ShouldThrowAlreadyExistsException() {
        when(categoryRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> categoryService.create(testCreationRequest));

        verify(categoryRepository, times(1)).existsByName(anyString());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Update Category - Success")
    void updateCategory_ShouldReturnUpdatedResponse() {
        Category initialCategory = new Category();
        initialCategory.setId(1L);
        initialCategory.setName("Đồ uống cũ");
        initialCategory.setDescription("Mô tả cũ");
        initialCategory.setImageUrl("url_cu.jpg");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName(testUpdateRequest.getName());
        updatedCategory.setDescription(testUpdateRequest.getDescription());
        updatedCategory.setImageUrl(initialCategory.getImageUrl());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(initialCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        Category result = categoryService.update(1L, testUpdateRequest);

        assertNotNull(result);
        assertEquals(testUpdateRequest.getName(), result.getName());
        assertEquals(testUpdateRequest.getDescription(), result.getDescription());
        assertEquals(initialCategory.getImageUrl(), result.getImageUrl());

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Update Category - Not Found")
    void update_ShouldThrowNotFoundException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.update(99L, testUpdateRequest));

        verify(categoryRepository, times(1)).findById(anyLong());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Delete Category - Success")
    void delete_ShouldNotThrowException() {
        when(categoryRepository.existsById(anyLong())).thenReturn(true);
        when(categoryRepository.existsByIdAndProductsIsNotEmpty(anyLong())).thenReturn(false);
        doNothing().when(categoryRepository).deleteById(anyLong());

        assertDoesNotThrow(() -> categoryService.delete(1L));

        verify(categoryRepository, times(1)).existsById(anyLong());
        verify(categoryRepository, times(1)).existsByIdAndProductsIsNotEmpty(anyLong());
        verify(categoryRepository, times(1)).deleteById(anyLong());
    }

    @Test
    @DisplayName("Delete Category - Not Found")
    void delete_ShouldThrowNotFoundException() {
        when(categoryRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> categoryService.delete(99L));

        verify(categoryRepository, times(1)).existsById(anyLong());
        verify(categoryRepository, never()).existsByIdAndProductsIsNotEmpty(anyLong());
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Delete Category - Has Products")
    void delete_ShouldThrowBadRequestException_WhenHasProducts() {
        when(categoryRepository.existsById(anyLong())).thenReturn(true);
        when(categoryRepository.existsByIdAndProductsIsNotEmpty(anyLong())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> categoryService.delete(1L));

        verify(categoryRepository, times(1)).existsById(anyLong());
        verify(categoryRepository, times(1)).existsByIdAndProductsIsNotEmpty(anyLong());
        verify(categoryRepository, never()).deleteById(anyLong());
    }
}
