package hoanght.posapi.dto.ordertable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderTableUpdateRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String name;
}
