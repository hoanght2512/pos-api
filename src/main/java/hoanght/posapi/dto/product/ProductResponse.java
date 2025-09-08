package hoanght.posapi.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import hoanght.posapi.dto.menu.MenuCategoryDto;
import hoanght.posapi.dto.menu.MenuInventoryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonPropertyOrder({"id", "name", "sku", "price", "description", "image_url", "unit", "category", "inventory"})
public class ProductResponse extends RepresentationModel<ProductResponse> {
    private Long id;
    private String name;
    private String sku;
    private BigDecimal price;
    private String description;
    @JsonProperty("is_available")
    private Boolean isAvailable;
    @JsonProperty("image_url")
    private String imageUrl;
    private String unit;
    private MenuCategoryDto category;
    private MenuInventoryDto inventory;
}
