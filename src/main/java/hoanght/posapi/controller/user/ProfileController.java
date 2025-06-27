package hoanght.posapi.controller.user;

import hoanght.posapi.dto.ChangePasswordRequest;
import hoanght.posapi.dto.DataResponse;
import hoanght.posapi.dto.UserProfileRequest;
import hoanght.posapi.dto.UserProfileResponse;
import hoanght.posapi.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/user/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<DataResponse<EntityModel<UserProfileResponse>>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserProfileResponse userProfileResponse = profileService.getUserProfile(userDetails);
        EntityModel<UserProfileResponse> entityModel = EntityModel.of(userProfileResponse,
                linkTo(methodOn(ProfileController.class).getProfile(userDetails)).withSelfRel(),
                linkTo(methodOn(ProfileController.class).updateProfile(userDetails, null)).withRel("update"),
                linkTo(methodOn(ProfileController.class).changePassword(userDetails, null)).withRel("change-password")
        );
        DataResponse<EntityModel<UserProfileResponse>> response = DataResponse.success("Profile retrieved successfully", entityModel);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<DataResponse<EntityModel<UserProfileResponse>>> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserProfileRequest userProfileRequest) {
        UserProfileResponse updatedProfile = profileService.updateUserProfile(userDetails, userProfileRequest);
        EntityModel<UserProfileResponse> entityModel = EntityModel.of(updatedProfile,
                linkTo(methodOn(ProfileController.class).getProfile(userDetails)).withSelfRel(),
                linkTo(methodOn(ProfileController.class).updateProfile(userDetails, null)).withRel("update"),
                linkTo(methodOn(ProfileController.class).changePassword(userDetails, null)).withRel("change-password")
        );
        DataResponse<EntityModel<UserProfileResponse>> response = DataResponse.success("Profile updated successfully", entityModel);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    public ResponseEntity<DataResponse<Void>> changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                                             @RequestBody ChangePasswordRequest changePasswordRequest) {
        profileService.changePassword(userDetails, changePasswordRequest);
        return ResponseEntity.ok(DataResponse.success("Password changed successfully", null));
    }
}
