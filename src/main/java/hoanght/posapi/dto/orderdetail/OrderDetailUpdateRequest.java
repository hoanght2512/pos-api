package hoanght.posapi.dto.orderdetail;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailUpdateRequest {
    @DecimalMin("0.1")
    private BigDecimal quantity;

    @DecimalMin(value = "0.0")
    private BigDecimal price;

    private String note;

    private String reason;
}
