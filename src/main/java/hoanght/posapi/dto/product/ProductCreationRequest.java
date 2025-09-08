package hoanght.posapi.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import hoanght.posapi.common.ProductUnit;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreationRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    private String description;

    private ProductUnit unit;

    private String sku;

    @NotNull
    private Boolean countable;

    @DecimalMin("0.1")
    private BigDecimal quantity;

    @DecimalMin(value = "0.0")
    private BigDecimal price;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("category_id")
    @NotNull
    @Min(1)
    private Long categoryId;
}
