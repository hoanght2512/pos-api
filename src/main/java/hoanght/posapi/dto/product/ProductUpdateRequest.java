package hoanght.posapi.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import hoanght.posapi.common.ProductUnit;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {
    private String name;

    private String description;

    private ProductUnit unit;

    private String sku;

    private Boolean countable;

    @DecimalMin("0.1")
    private BigDecimal quantity;

    @DecimalMin("0.0")
    private BigDecimal price;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("category_id")
    private Long categoryId;
}
