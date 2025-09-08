package hoanght.posapi.dto.tablesession;

import hoanght.posapi.dto.orderdetail.OrderDetailCreationItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AddProductsRequest {
    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<OrderDetailCreationItem> items;
}
