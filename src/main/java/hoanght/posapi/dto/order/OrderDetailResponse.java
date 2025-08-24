package hoanght.posapi.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonPropertyOrder({
        "id",
        "product",
        "quantity",
        "note",
        "price"
})
public class OrderDetailResponse {
    private Long id;
    @JsonProperty("product")
    private OrderDetailProductResponse product;
    private Long quantity;
    private String note;
    @JsonProperty("price")
    private BigDecimal priceAtOrder;
}