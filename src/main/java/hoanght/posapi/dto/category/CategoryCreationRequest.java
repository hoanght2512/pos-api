package hoanght.posapi.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryCreationRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String name;

    private String description;

    private String imageUrl;
}
