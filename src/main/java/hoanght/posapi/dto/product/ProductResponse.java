package hoanght.posapi.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import hoanght.posapi.dto.category.CategoryResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductResponse extends RepresentationModel<ProductResponse> {
    public CategoryResponse category;
    private Long id;
    private String name;
    private String sku;
    private BigDecimal price;
    private String description;
    @JsonProperty("image_url")
    private String imageUrl;
}
