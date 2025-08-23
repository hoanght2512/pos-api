package hoanght.posapi.dto.tablesession;

import com.fasterxml.jackson.annotation.JsonProperty;
import hoanght.posapi.common.TableStatus;
import hoanght.posapi.dto.order.OrderResponse;
import hoanght.posapi.dto.ordertable.OrderTableResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TableSessionResponse extends OrderTableResponse {
    private TableStatus status;
    @JsonProperty("order")
    private OrderResponse order;
}
