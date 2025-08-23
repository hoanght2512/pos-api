package hoanght.posapi.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonPropertyOrder({
    "id",
    "name",
    "sku",
    "price",
    "description",
    "image_url",
    "countable",
    "category",
    "inventory"
})
public class ProductResponse extends RepresentationModel<ProductResponse> {
    private Long id;
    private String name;
    private String sku;
    private BigDecimal price;
    private String description;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("countable")
    private Boolean countable;
    private ProductCategoryResponse category;
    private InventoryResponse inventory;
}
