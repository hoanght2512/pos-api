package hoanght.posapi.assembler;

import hoanght.posapi.controller.admin.ProductAdminController;
import hoanght.posapi.dto.product.ProductResponse;
import hoanght.posapi.model.Product;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@Component
public class ProductAssembler extends RepresentationModelAssemblerSupport<Product, ProductResponse> {
    private final ModelMapper modelMapper;

    public ProductAssembler(ModelMapper modelMapper) {
        super(Product.class, ProductResponse.class);
        this.modelMapper = modelMapper;
    }

    @Override
    @NonNull
    public ProductResponse toModel(@NonNull Product product) {
        ProductResponse response = modelMapper.map(product, ProductResponse.class);
        response.add(linkTo(methodOn(ProductAdminController.class).findProductById(product.getId())).withSelfRel());
        response.add(linkTo(methodOn(ProductAdminController.class).updateProduct(product.getId(), null)).withRel("update"));
        response.add(linkTo(methodOn(ProductAdminController.class).deleteProduct(product.getId())).withRel("delete"));
        return response;
    }
}
