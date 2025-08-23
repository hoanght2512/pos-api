package hoanght.posapi.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import hoanght.posapi.common.OrderStatus;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    @JsonProperty("created_at")
    private Instant createdAt;
    @JsonProperty("updated_at")
    private Instant updatedAt;
    @JsonProperty("status")
    private OrderStatus status;
    @JsonProperty("details")
    private List<OrderDetailResponse> orderDetails;
}