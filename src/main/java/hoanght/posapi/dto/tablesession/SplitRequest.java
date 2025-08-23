package hoanght.posapi.dto.tablesession;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SplitRequest {
    @NotNull(message = "From Order ID cannot be null")
    private Long fromOrderId;

    @NotNull(message = "To Table ID cannot be null")
    private Long toTableId;

    @NotEmpty(message = "Details to move cannot be empty")
    @Valid
    private List<DetailToMove> detailsToMove;

    @Data
    public static class DetailToMove {
        @NotNull(message = "Order Detail ID cannot be null")
        private Long orderDetailId;

        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity to move must be at least 1")
        private Long quantity;
    }
}
