package hoanght.posapi.dto.tablesession;

import hoanght.posapi.dto.orderdetail.OrderDetailSplitItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SplitRequest {
    @NotEmpty(message = "Details to move cannot be empty")
    @Valid
    private List<OrderDetailSplitItem> details;
}
