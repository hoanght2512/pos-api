package hoanght.posapi.controller;

import hoanght.posapi.dto.request.ChangePasswordRequest;
import hoanght.posapi.dto.request.ProfileUpdateRequest;
import hoanght.posapi.dto.response.DataResponse;
import hoanght.posapi.dto.response.ProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/v1/user/profile")
@Tag(name = "Profile", description = "Profile management APIs")
public interface IProfileController {
    @GetMapping
    @Operation(summary = "Get user profile", description = "Retrieve the profile of the authenticated user")
    ResponseEntity<DataResponse<EntityModel<ProfileResponse>>> getProfile(@AuthenticationPrincipal UserDetails userDetails);

    @PutMapping
    @Operation(summary = "Update user profile", description = "Update the profile of the authenticated user")
    ResponseEntity<DataResponse<EntityModel<ProfileResponse>>> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody ProfileUpdateRequest profileUpdateRequest);

    @PutMapping("/change-password")
    @Operation(summary = "Change user password", description = "Change the password of the authenticated user")
    ResponseEntity<DataResponse<Void>> changePassword(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody ChangePasswordRequest changePasswordRequest);
}
