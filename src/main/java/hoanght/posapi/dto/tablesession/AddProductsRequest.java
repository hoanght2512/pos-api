package hoanght.posapi.dto.tablesession;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddProductsRequest {
    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<Item> items;

    @Data
    public static class Item {
        @NotNull(message = "Product ID cannot be null")
        @JsonProperty("product_id")
        private Long productId;

        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Long quantity;

        private String note;
    }
}
