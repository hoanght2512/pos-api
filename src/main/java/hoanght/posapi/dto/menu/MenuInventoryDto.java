package hoanght.posapi.dto.menu;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class MenuInventoryDto implements Serializable {
    private BigDecimal quantity;
}
