package hoanght.posapi.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileResponse {
    private String username;
    private String email;
    @JsonProperty("full_name")
    private String fullName;
}
