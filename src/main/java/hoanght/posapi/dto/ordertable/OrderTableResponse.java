package hoanght.posapi.dto.ordertable;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@JsonPropertyOrder({"id", "name"})
@NoArgsConstructor
@AllArgsConstructor
public class OrderTableResponse extends RepresentationModel<OrderTableResponse> {
    private Long id;
    private String name;
}