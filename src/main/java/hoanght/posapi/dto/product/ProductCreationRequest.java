package hoanght.posapi.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreationRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    private String description;

    private String sku;

    @DecimalMin(value = "0.0")
    private BigDecimal price;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("category_id")
    @NotNull
    @Min(1)
    private Long categoryId;
}
