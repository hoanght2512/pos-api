package hoanght.posapi.controller.impl;

import hoanght.posapi.controller.ICheckController;
import hoanght.posapi.dto.request.EmailRequest;
import hoanght.posapi.dto.request.UsernameRequest;
import hoanght.posapi.dto.response.DataResponse;
import hoanght.posapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class CheckController implements ICheckController {
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<DataResponse<Void>> checkUsername(UsernameRequest usernameRequest) {
        boolean exists = userRepository.existsByUsername(usernameRequest.getUsername());
        DataResponse<Void> response = exists ? DataResponse.error(HttpStatus.CONFLICT.value(), "Validation Failed", "Username already exists", Instant.now(), null) : DataResponse.success("Username is available", null);
        return new ResponseEntity<>(response, exists ? HttpStatus.CONFLICT : HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DataResponse<Void>> checkEmail(EmailRequest emailRequest) {
        boolean exists = userRepository.existsByEmail(emailRequest.getEmail());
        DataResponse<Void> response = exists ? DataResponse.error(HttpStatus.CONFLICT.value(), "Validation Failed", "Email already exists", Instant.now(), null) : DataResponse.success("Email is available", null);
        return new ResponseEntity<>(response, exists ? HttpStatus.CONFLICT : HttpStatus.OK);
    }
}
