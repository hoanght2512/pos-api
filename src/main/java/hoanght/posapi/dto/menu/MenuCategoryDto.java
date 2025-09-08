package hoanght.posapi.dto.menu;

import lombok.Data;

import java.io.Serializable;

@Data
public class MenuCategoryDto implements Serializable {
    private Long id;
    private String name;
}
