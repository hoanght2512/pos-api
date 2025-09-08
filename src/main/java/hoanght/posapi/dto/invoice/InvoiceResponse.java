package hoanght.posapi.dto.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import hoanght.posapi.common.InvoiceStatus;
import hoanght.posapi.common.PaymentMethod;
import hoanght.posapi.dto.order.OrderResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonPropertyOrder({"id", "created_at", "updated_at", "created_by", "updated_by", "status", "payment_method", "order_table", "order", "total_amount"})
public class InvoiceResponse extends RepresentationModel<InvoiceResponse> {
    private Long id;
    @JsonProperty("created_at")
    private Instant createdAt;
    @JsonProperty("updated_at")
    private Instant updatedAt;
    @JsonProperty("created_by")
    private String createdBy;
    @JsonProperty("updated_by")
    private String updatedBy;
    private InvoiceStatus status;
    @JsonProperty("payment_method")
    private PaymentMethod paymentMethod;
    @JsonProperty("order_table")
    private String orderTable;
    private OrderResponse order;
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
}
