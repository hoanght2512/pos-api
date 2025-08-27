package hoanght.posapi.dto.orderdetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailSplitItem {
    @NotNull
    @JsonProperty("detail_id")
    private Long orderDetailId;

    @NotNull
    @DecimalMin("0.1")
    private BigDecimal quantity;
}
