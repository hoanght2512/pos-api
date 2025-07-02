package hoanght.posapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @JsonProperty("username")
    private String fullName;
}
