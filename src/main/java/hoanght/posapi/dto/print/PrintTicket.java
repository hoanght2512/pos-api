package hoanght.posapi.dto.print;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PrintTicket {
    private String content;
    private BigDecimal quantity;
    private String note;
}
