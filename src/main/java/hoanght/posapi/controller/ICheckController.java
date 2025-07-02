package hoanght.posapi.controller;

import hoanght.posapi.dto.request.EmailRequest;
import hoanght.posapi.dto.request.UsernameRequest;
import hoanght.posapi.dto.response.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/v1/public")
@Tag(name = "Check", description = "Check operations for username and email availability")
public interface ICheckController {
    @GetMapping("/check-username")
    @Operation(summary = "Check Username", description = "Check if a username is available")
    ResponseEntity<DataResponse<Void>> checkUsername(@Valid @RequestBody UsernameRequest usernameRequest);

    @GetMapping("/check-email")
    @Operation(summary = "Check Email", description = "Check if an email is available")
    ResponseEntity<DataResponse<Void>> checkEmail(@Valid @RequestBody EmailRequest emailRequest);
}
