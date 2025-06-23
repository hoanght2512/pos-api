package hoanght.posapi.dto;

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
    private String path;
    private Map<String, String> details;
    private T data;

    public static <T> DataResponse<T> success(T data) {
        return DataResponse.<T>builder()
                .success(true)
                .status(200)
                .message("Request was successful")
                .timestamp(Instant.now())
                .data(data)
                .build();
    }

    public static <T> DataResponse<T> success(String message, T data) {
        return DataResponse.<T>builder()
                .success(true)
                .status(200)
                .message(message)
                .timestamp(Instant.now())
                .data(data)
                .build();
    }

    public static <T> DataResponse<T> created(T data) {
        return DataResponse.<T>builder()
                .success(true)
                .status(201)
                .message("Resource created successfully")
                .timestamp(Instant.now())
                .data(data)
                .build();
    }

    public static <T> DataResponse<T> created(String message, T data) {
        return DataResponse.<T>builder()
                .success(true)
                .status(201)
                .message(message)
                .timestamp(Instant.now())
                .data(data)
                .build();
    }

    public static <T> DataResponse<T> error(int status, String error, String message, Instant timestamp, String path) {
        return DataResponse.<T>builder()
                .success(false)
                .status(status)
                .error(error)
                .message(message)
                .timestamp(timestamp)
                .path(path)
                .build();
    }

    public static <T> DataResponse<T> error(int status, String error, String message, Instant timestamp, String path, Map<String, String> details) {
        return DataResponse.<T>builder()
                .success(false)
                .status(status)
                .error(error)
                .message(message)
                .timestamp(timestamp)
                .path(path)
                .details(details)
                .build();
    }
}