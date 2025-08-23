package hoanght.posapi.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link hoanght.posapi.model.Category}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryResponse implements Serializable {
    private Long id;
    private String name;
}