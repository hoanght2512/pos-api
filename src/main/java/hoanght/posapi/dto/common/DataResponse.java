package hoanght.posapi.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataResponse<T> {
    private boolean success;
    private int status;
    private String message;
    private Instant timestamp;
    private String error;
    private Map<String, String> details;
    private T data;

    public static <T> DataResponse<T> success(T data) {
        return DataResponse.<T>builder().success(true).status(200).message("Fetch data successfully").timestamp(Instant.now()).data(data).build();
    }

    public static <T> DataResponse<T> success(String message) {
        return DataResponse.<T>builder().success(true).status(200).message(message).timestamp(Instant.now()).build();
    }

    public static <T> DataResponse<T> success(String message, T data) {
        return DataResponse.<T>builder().success(true).status(200).message(message).timestamp(Instant.now()).data(data).build();
    }

    public static <T> DataResponse<T> error(int status, String error) {
        return DataResponse.<T>builder().success(false).status(status).error(error).timestamp(Instant.now()).build();
    }

    public static <T> DataResponse<T> error(int status, String error, Map<String, String> details) {
        return DataResponse.<T>builder().success(false).status(status).error(error).timestamp(Instant.now()).details(details).build();
    }
}