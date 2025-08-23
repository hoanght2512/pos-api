package hoanght.posapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hoanght.posapi.dto.category.CategoryCreationRequest;
import hoanght.posapi.dto.category.CategoryUpdateRequest;
import hoanght.posapi.exception.AlreadyExistsException;
import hoanght.posapi.exception.BadRequestException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.model.Category;
import hoanght.posapi.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CategoryController Tests")
@Tag("Integration")
public class CategoryAdminControllerTests {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private Category category1;
    private Category category2;
    private CategoryCreationRequest createRequest;
    private CategoryUpdateRequest updateRequest;
    private Category updatedResponse;

    @BeforeEach
    public void setUp() {
        reset(categoryService);

        category1 = new Category();
        category1.setId(1L);
        category1.setName("Drinks");
        category1.setDescription("Beverages category");
        category1.setImageUrl("http://localhost/images/drinks.jpg");

        category2 = new Category();
        category2.setId(2L);
        category2.setName("Food");
        category2.setDescription("Food items category");
        category2.setImageUrl("http://localhost/images/food.jpg");

        List<Category> categoriesList = Arrays.asList(category1, category2);
        Page<Category> mockCategoryPageWithLinks = new PageImpl<>(categoriesList);

        createRequest = new CategoryCreationRequest();
        createRequest.setName("Desserts");
        createRequest.setDescription("Sweet treats category");
        createRequest.setImageUrl("http://localhost/images/desserts.jpg");

        updateRequest = new CategoryUpdateRequest();
        updateRequest.setName("Updated Drinks");
        updateRequest.setDescription("Updated beverages category");

        updatedResponse = new Category();
        updatedResponse.setId(1L);
        updatedResponse.setName("Updated Drinks");
        updatedResponse.setDescription("Updated beverages category");
        updatedResponse.setImageUrl("http://localhost/images/drinks.jpg");

        when(categoryService.findAll(any(Pageable.class))).thenReturn(mockCategoryPageWithLinks);
        when(categoryService.findById(1L)).thenReturn(category1);
        when(categoryService.findById(2L)).thenReturn(category2);
        when(categoryService.create(any(CategoryCreationRequest.class))).thenReturn(category2);
        when(categoryService.update(eq(1L), any(CategoryUpdateRequest.class))).thenReturn(updatedResponse);
        doNothing().when(categoryService).delete(anyLong());
    }

    @Test
    @DisplayName("GET /v1/admin/categories - Access denied for USER role")
    @WithMockUser(roles = "USER")
    public void testFindAllCategoriesAccessDeniedForUserRole() throws Exception {
        mockMvc.perform(get("/v1/admin/categories"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /v1/admin/categories - Access granted for ADMIN role")
    @WithMockUser(roles = "ADMIN")
    public void testFindAllCategoriesAccessGrantedForAdminRole() throws Exception {
        mockMvc.perform(get("/v1/admin/categories"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /v1/admin/categories - Returns paginated list of categories")
    @WithMockUser(roles = "ADMIN")
    public void testFindAllCategoriesReturnsPaginatedListWithHateoasLinks() throws Exception {
        mockMvc.perform(get("/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Get data successfully"))
                .andExpect(jsonPath("$.data.links[0].href").value("http://localhost/v1/admin/categories"))
                .andExpect(jsonPath("$.data.content[0].id").value(1L))
                .andExpect(jsonPath("$.data.content[0].name").value("Drinks"))
                .andExpect(jsonPath("$.data.content[0].description").value("Beverages category"))
                .andExpect(jsonPath("$.data.content[0].image_url").value("http://localhost/images/drinks.jpg"))
                .andExpect(jsonPath("$.data.content[0].links[0].rel").value("self"))
                .andExpect(jsonPath("$.data.content[0].links[0].href").value("http://localhost/v1/admin/categories/1"))
                .andExpect(jsonPath("$.data.content[0].links[1].rel").value("update"))
                .andExpect(jsonPath("$.data.content[0].links[1].href").value("http://localhost/v1/admin/categories/1"))
                .andExpect(jsonPath("$.data.content[0].links[2].rel").value("delete"))
                .andExpect(jsonPath("$.data.content[0].links[2].href").value("http://localhost/v1/admin/categories/1"))
                .andExpect(jsonPath("$.data.content[1].id").value(2L))
                .andExpect(jsonPath("$.data.content[1].name").value("Food"))
                .andExpect(jsonPath("$.data.content[1].description").value("Food items category"))
                .andExpect(jsonPath("$.data.content[1].image_url").value("http://localhost/images/food.jpg"))
                .andExpect(jsonPath("$.data.content[1].links[0].rel").value("self"))
                .andExpect(jsonPath("$.data.content[1].links[0].href").value("http://localhost/v1/admin/categories/2"))
                .andExpect(jsonPath("$.data.content[1].links[1].rel").value("update"))
                .andExpect(jsonPath("$.data.content[1].links[1].href").value("http://localhost/v1/admin/categories/2"))
                .andExpect(jsonPath("$.data.content[1].links[2].rel").value("delete"))
                .andExpect(jsonPath("$.data.content[1].links[2].href").value("http://localhost/v1/admin/categories/2"))
                .andExpect(jsonPath("$.data.page.size").value(2))
                .andExpect(jsonPath("$.data.page.totalElements").value(2))
                .andExpect(jsonPath("$.data.page.totalPages").value(1))
                .andExpect(jsonPath("$.data.page.number").value(0));

        verify(categoryService, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /v1/admin/categories/{id} - Returns a single category with HATEOAS links")
    @WithMockUser(roles = "ADMIN")
    public void testFindCategoryByIdReturnsCategoryWithHateoasLinks() throws Exception {
        mockMvc.perform(get("/v1/admin/categories/{categoryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(category1.getId()))
                .andExpect(jsonPath("$.data.name").value(category1.getName()))
                .andExpect(jsonPath("$.data.description").value(category1.getDescription()))
                .andExpect(jsonPath("$.data.image_url").value(category1.getImageUrl()))

                .andExpect(jsonPath("$.data.links[0].rel").value("self"))
                .andExpect(jsonPath("$.data.links[0].href").value("http://localhost/v1/admin/categories/" + category1.getId()))
                .andExpect(jsonPath("$.data.links[1].rel").value("update"))
                .andExpect(jsonPath("$.data.links[1].href").value("http://localhost/v1/admin/categories/" + category1.getId()))
                .andExpect(jsonPath("$.data.links[2].rel").value("delete"))
                .andExpect(jsonPath("$.data.links[2].href").value("http://localhost/v1/admin/categories/" + category1.getId()))
                .andExpect(jsonPath("$.data.links[3].rel").value("all"))
                .andExpect(jsonPath("$.data.links[3].href").value("http://localhost/v1/admin/categories"));

        verify(categoryService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /v1/admin/categories/{id} - Returns 404 Not Found")
    @WithMockUser(roles = "ADMIN")
    public void testFindCategoryByIdNotFound() throws Exception {
        when(categoryService.findById(99L)).thenThrow(new NotFoundException("Category with ID 99 not found"));

        mockMvc.perform(get("/v1/admin/categories/{categoryId}", 99L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Category with ID 99 not found"));

        verify(categoryService, times(1)).findById(99L);
    }

    @Test
    @DisplayName("POST /v1/admin/categories - Creates a new category with HATEOAS links")
    @WithMockUser(roles = "ADMIN")
    public void testCreateCategoryCreatesNewCategoryWithHateoasLinks() throws Exception {
        mockMvc.perform(post("/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Category created successfully"))
                .andExpect(jsonPath("$.data.id").value(category2.getId()))
                .andExpect(jsonPath("$.data.name").value(category2.getName()))
                .andExpect(jsonPath("$.data.description").value(category2.getDescription()))
                .andExpect(jsonPath("$.data.image_url").value(category2.getImageUrl()))
                .andExpect(jsonPath("$.data.links[0].rel").value("self"))
                .andExpect(jsonPath("$.data.links[0].href").value("http://localhost/v1/admin/categories/" + category2.getId()))
                .andExpect(jsonPath("$.data.links[1].rel").value("update"))
                .andExpect(jsonPath("$.data.links[1].href").value("http://localhost/v1/admin/categories/" + category2.getId()))
                .andExpect(jsonPath("$.data.links[2].rel").value("delete"))
                .andExpect(jsonPath("$.data.links[2].href").value("http://localhost/v1/admin/categories/" + category2.getId()))
                .andExpect(jsonPath("$.data.links[3].rel").value("all"))
                .andExpect(jsonPath("$.data.links[3].href").value("http://localhost/v1/admin/categories"));

        verify(categoryService, times(1)).create(any(CategoryCreationRequest.class));
    }

    @Test
    @DisplayName("POST /v1/admin/categories - Returns 400 Bad Request for invalid input")
    @WithMockUser(roles = "ADMIN")
    public void testCreateCategoryReturnsBadRequestForInvalidInput() throws Exception {
        CategoryCreationRequest invalidRequest = new CategoryCreationRequest();
        invalidRequest.setName("");
        invalidRequest.setDescription("Some description");

        mockMvc.perform(post("/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(categoryService, never()).create(any(CategoryCreationRequest.class));
    }

    @Test
    @DisplayName("POST /v1/admin/categories - Returns 409 Conflict if category name already exists")
    @WithMockUser(roles = "ADMIN")
    public void testCreateCategoryReturnsConflictIfNameExists() throws Exception {
        when(categoryService.create(any(CategoryCreationRequest.class)))
                .thenThrow(new AlreadyExistsException("Category with name Desserts already exists"));

        mockMvc.perform(post("/v1/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Category with name Desserts already exists"));

        verify(categoryService, times(1)).create(any(CategoryCreationRequest.class));
    }

    @Test
    @DisplayName("PUT /v1/admin/categories/{id} - Updates an existing category with HATEOAS links")
    @WithMockUser(roles = "ADMIN")
    public void testUpdateCategoryUpdatesExistingCategoryWithHateoasLinks() throws Exception {
        mockMvc.perform(put("/v1/admin/categories/{categoryId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category updated successfully"))
                .andExpect(jsonPath("$.data.id").value(updatedResponse.getId()))
                .andExpect(jsonPath("$.data.name").value(updatedResponse.getName()))
                .andExpect(jsonPath("$.data.description").value(updatedResponse.getDescription()))
                .andExpect(jsonPath("$.data.links[0].rel").value("self"))
                .andExpect(jsonPath("$.data.links[0].href").value("http://localhost/v1/admin/categories/" + updatedResponse.getId()))
                .andExpect(jsonPath("$.data.links[1].rel").value("update"))
                .andExpect(jsonPath("$.data.links[1].href").value("http://localhost/v1/admin/categories/" + updatedResponse.getId()))
                .andExpect(jsonPath("$.data.links[2].rel").value("delete"))
                .andExpect(jsonPath("$.data.links[2].href").value("http://localhost/v1/admin/categories/" + updatedResponse.getId()))
                .andExpect(jsonPath("$.data.links[3].rel").value("all"))
                .andExpect(jsonPath("$.data.links[3].href").value("http://localhost/v1/admin/categories"));

        verify(categoryService, times(1)).update(eq(1L), any(CategoryUpdateRequest.class));
    }

    @Test
    @DisplayName("PUT /v1/admin/categories/{id} - Returns 404 Not Found for non-existent category")
    @WithMockUser(roles = "ADMIN")
    public void testUpdateCategoryNotFound() throws Exception {
        when(categoryService.update(eq(99L), any(CategoryUpdateRequest.class)))
                .thenThrow(new NotFoundException("Category with ID 99 not found"));

        mockMvc.perform(put("/v1/admin/categories/{categoryId}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Category with ID 99 not found"));

        verify(categoryService, times(1)).update(eq(99L), any(CategoryUpdateRequest.class));
    }

    @Test
    @DisplayName("DELETE /v1/admin/categories/{id} - Deletes a category successfully")
    @WithMockUser(roles = "ADMIN")
    public void testDeleteCategorySuccessfully() throws Exception {
        mockMvc.perform(delete("/v1/admin/categories/{categoryId}", 1L))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("DELETE /v1/admin/categories/{id} - Returns 404 Not Found for non-existent category")
    @WithMockUser(roles = "ADMIN")
    public void testDeleteCategoryNotFound() throws Exception {
        doThrow(new NotFoundException("Category with ID 99 not found"))
                .when(categoryService).delete(99L);

        mockMvc.perform(delete("/v1/admin/categories/{categoryId}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Category with ID 99 not found"));

        verify(categoryService, times(1)).delete(99L);
    }

    @Test
    @DisplayName("DELETE /v1/admin/categories/{id} - Returns 400 Bad Request if category has products")
    @WithMockUser(roles = "ADMIN")
    public void testDeleteCategoryReturnsBadRequestIfHasProducts() throws Exception {
        doThrow(new BadRequestException("Cannot delete category with ID 1 because it has associated products"))
                .when(categoryService).delete(1L);

        mockMvc.perform(delete("/v1/admin/categories/{categoryId}", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cannot delete category with ID 1 because it has associated products"));

        verify(categoryService, times(1)).delete(1L);
    }

}
