package hoanght.posapi.dto.menu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class MenuResponse implements Serializable {
    private String name;
    private String description;
    @JsonProperty("image_url")
    private String imageUrl;
    private BigDecimal price;
    private String sku;
    private MenuCategoryDto category;
    private MenuInventoryDto inventory;
}
