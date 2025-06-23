package hoanght.posapi.exception;

import hoanght.posapi.dto.DataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DataResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        log.debug(errors.toString());
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), "Validation Failed", Instant.now(), request.getDescription(false), errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<DataResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage(), Instant.now(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<DataResponse<Void>> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), ex.getMessage(), Instant.now(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DataResponse<Void>> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        log.debug("Bad credentials: {}", ex.getMessage());
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), "Invalid username or password.", Instant.now(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<DataResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage(), Instant.now(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResponse<Void>> handleGlobalException(Exception ex, WebRequest request) {
        DataResponse<Void> errorResponse = DataResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "An unexpected error occurred. Please try again later.", // Thông báo lỗi chung
                Instant.now(), request.getDescription(false));
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
