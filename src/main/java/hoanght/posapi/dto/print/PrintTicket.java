package hoanght.posapi.dto.print;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrintTicket {
    private String content;
    private Long quantity;
    private String note;
}
