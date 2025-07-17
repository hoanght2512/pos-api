package hoanght.posapi.controller;

import hoanght.posapi.dto.common.DataResponse;
import hoanght.posapi.repository.jpa.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/public")
@Tag(name = "Check Availability", description = "Check operations for username and email availability")
public class CheckController {
    private final UserRepository userRepository;

    @GetMapping("/check-username")
    @Operation(summary = "Check Username", description = "Check if a username is available")
    @Parameters({
            @Parameter(name = "username", description = "Username to check availability", required = true)
    })
    public ResponseEntity<DataResponse<Void>> checkUsernameAvailability(@Parameter(description = "Username to check availability") @RequestParam(value = "username") String username) {
        boolean exists = userRepository.existsByUsername(username);
        DataResponse<Void> response = exists ? DataResponse.error(HttpStatus.CONFLICT.value(), "Username already exists") : DataResponse.success("Username is available");
        return new ResponseEntity<>(response, exists ? HttpStatus.CONFLICT : HttpStatus.OK);
    }

    @GetMapping("/check-email")
    @Operation(summary = "Check Email", description = "Check if an email is available")
    public ResponseEntity<DataResponse<Void>> checkEmailAvailability(@Parameter(description = "Email to check availability") @RequestParam("email") String email) {
        boolean exists = userRepository.existsByEmail(email);
        DataResponse<Void> response = exists ? DataResponse.error(HttpStatus.CONFLICT.value(), "Email already exists") : DataResponse.success("Email is available");
        return new ResponseEntity<>(response, exists ? HttpStatus.CONFLICT : HttpStatus.OK);
    }
}
