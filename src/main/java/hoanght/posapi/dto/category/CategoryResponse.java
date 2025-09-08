package hoanght.posapi.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Data
public class CategoryResponse extends RepresentationModel<CategoryResponse> {
    private Long id;
    private String name;
    private String description;
    private String slug;
    @JsonProperty("image_url")
    private String imageUrl;
}
