package hoanght.posapi.dto.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductDto implements Serializable {
    private Long id;
    private String name;
    private String unit;
}
