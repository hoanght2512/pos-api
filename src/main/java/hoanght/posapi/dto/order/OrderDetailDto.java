package hoanght.posapi.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonPropertyOrder({
        "id",
        "product",
        "quantity",
        "note",
        "price"
})
public class OrderDetailDto extends RepresentationModel<OrderDetailDto> {
    private Long id;
    @JsonProperty("product")
    private ProductDto product;
    private BigDecimal quantity;
    private String note;
    @JsonProperty("price")
    private BigDecimal price;
}