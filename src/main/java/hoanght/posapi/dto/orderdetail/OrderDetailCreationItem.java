package hoanght.posapi.dto.orderdetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderDetailCreationItem {
    @NotNull(message = "Product ID cannot be null")
    @JsonProperty("product_id")
    private Long productId;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Long quantity;

    private String note;
}
