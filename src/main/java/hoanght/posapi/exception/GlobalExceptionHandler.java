package hoanght.posapi.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import hoanght.posapi.dto.common.DataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.debug("Validation error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        Object targetObject = ex.getBindingResult().getTarget();
        Class<?> dtoClass = Objects.requireNonNull(targetObject).getClass();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String jsonFieldName = fieldName;
            try {
                Field field = dtoClass.getDeclaredField(fieldName);
                if (field.isAnnotationPresent(JsonProperty.class)) {
                    JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                    jsonFieldName = jsonProperty.value();
                }
            } catch (NoSuchFieldException e) {
                log.warn("No JsonProperty annotation found for field: {}", fieldName);
            }
            errors.put(jsonFieldName, error.getDefaultMessage());
        });
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid request data", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<DataResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.debug("Type mismatch error: {}", ex.getMessage());
        String errorMessage = String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName());
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<DataResponse<Void>> handleResourceAlreadyExistsException(AlreadyExistsException ex) {
        log.debug("Resource already exists: {}", ex.getMessage());
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<DataResponse<Void>> handleResourceNotFoundException(NotFoundException ex) {
        log.debug("Resource not found: {}", ex.getMessage());
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<DataResponse<Void>> handleBadRequestException(BadRequestException ex) {
        log.debug("Bad request: {}", ex.getMessage());
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<DataResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
        log.debug("Authentication error: {}", ex.getMessage());
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<DataResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.debug("Access denied: {}", ex.getMessage());
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.FORBIDDEN.value(), "Access denied");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DataResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.debug("Malformed JSON request: {}", ex.getMessage());
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.BAD_REQUEST.value(), "Malformed JSON request");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<DataResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.debug("HTTP method not supported: {}", ex.getMessage());
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.METHOD_NOT_ALLOWED.value(), "Method not allowed");
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<DataResponse<Void>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        log.debug("Unsupported media type: {}", ex.getMessage());
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "Unsupported media type");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<DataResponse<Void>> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.debug("No resource found: {}", ex.getMessage());
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<DataResponse<Void>> handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException ex) {
        log.debug("Optimistic locking failure: {}", ex.getMessage());
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.CONFLICT.value(), "Conflict occurred due to concurrent modification. Please retry.");
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResponse<Void>> handleGlobalException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
