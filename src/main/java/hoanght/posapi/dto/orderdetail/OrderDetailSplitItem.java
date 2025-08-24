package hoanght.posapi.dto.orderdetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderDetailSplitItem {
    @NotNull(message = "Order Detail ID cannot be null")
    @JsonProperty("detail_id")
    private Long orderDetailId;

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity to move must be at least 1")
    private Long quantity;
}
