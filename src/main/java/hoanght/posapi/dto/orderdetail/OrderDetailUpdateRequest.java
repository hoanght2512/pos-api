package hoanght.posapi.dto.orderdetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailUpdateRequest {
    @Min(value = 1, message = "Quantity must be at least 1")
    private Long quantity;
    @JsonProperty("price")
    @DecimalMin("1000.0")
    private BigDecimal price;
    private String note;
    private String reason;
}
