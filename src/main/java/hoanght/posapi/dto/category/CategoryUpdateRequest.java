package hoanght.posapi.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CategoryUpdateRequest {
    private String name;

    private String description;

    @JsonProperty("image_url")
    private String imageUrl;
}
