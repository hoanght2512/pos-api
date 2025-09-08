package hoanght.posapi.dto.orderdetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailCreationItem {
    @NotNull(message = "Product ID cannot be null")
    @JsonProperty("product_id")
    private Long productId;

    @NotNull(message = "Quantity cannot be null")
    @DecimalMin("0.1")
    private BigDecimal quantity;

    private String note;
}
