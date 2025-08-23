package hoanght.posapi.assembler;

import hoanght.posapi.controller.admin.CategoryAdminController;
import hoanght.posapi.dto.category.CategoryResponse;
import hoanght.posapi.model.Category;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoryAssembler extends RepresentationModelAssemblerSupport<Category, CategoryResponse> {
    private final ModelMapper modelMapper;

    public CategoryAssembler(ModelMapper modelMapper) {
        super(Category.class, CategoryResponse.class);
        this.modelMapper = modelMapper;
    }

    @Override
    @NonNull
    public CategoryResponse toModel(@NonNull Category category) {
        CategoryResponse response = modelMapper.map(category, CategoryResponse.class);
        response.add(linkTo(methodOn(CategoryAdminController.class).findCategoryById(category.getId())).withSelfRel());
        response.add(linkTo(methodOn(CategoryAdminController.class).updateCategory(category.getId(), null)).withRel("update"));
        response.add(linkTo(methodOn(CategoryAdminController.class).deleteCategory(category.getId())).withRel("delete"));
        return response;
    }
}
