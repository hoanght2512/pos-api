package hoanght.posapi.controller.impl;

import hoanght.posapi.controller.IProfileController;
import hoanght.posapi.dto.request.ChangePasswordRequest;
import hoanght.posapi.dto.request.ProfileUpdateRequest;
import hoanght.posapi.dto.response.DataResponse;
import hoanght.posapi.dto.response.ProfileResponse;
import hoanght.posapi.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class ProfileController implements IProfileController {
    private final ProfileService profileService;

    @Override
    public ResponseEntity<DataResponse<EntityModel<ProfileResponse>>> getProfile(UserDetails userDetails) {
        ProfileResponse profileResponse = profileService.getUserProfile(userDetails);
        EntityModel<ProfileResponse> entityModel = EntityModel.of(profileResponse,
                linkTo(methodOn(ProfileController.class).getProfile(userDetails)).withSelfRel(),
                linkTo(methodOn(ProfileController.class).updateProfile(userDetails, null)).withRel("update"),
                linkTo(methodOn(ProfileController.class).changePassword(userDetails, null)).withRel("change-password")
        );
        DataResponse<EntityModel<ProfileResponse>> response = DataResponse.success("Profile retrieved successfully", entityModel);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<DataResponse<EntityModel<ProfileResponse>>> updateProfile(UserDetails userDetails, ProfileUpdateRequest profileUpdateRequest) {
        ProfileResponse updatedProfile = profileService.updateUserProfile(userDetails, profileUpdateRequest);
        EntityModel<ProfileResponse> entityModel = EntityModel.of(updatedProfile,
                linkTo(methodOn(ProfileController.class).getProfile(userDetails)).withSelfRel(),
                linkTo(methodOn(ProfileController.class).updateProfile(userDetails, null)).withRel("update"),
                linkTo(methodOn(ProfileController.class).changePassword(userDetails, null)).withRel("change-password")
        );
        DataResponse<EntityModel<ProfileResponse>> response = DataResponse.success("Profile updated successfully", entityModel);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<DataResponse<Void>> changePassword(UserDetails userDetails, ChangePasswordRequest changePasswordRequest) {
        profileService.changePassword(userDetails, changePasswordRequest);
        return ResponseEntity.ok(DataResponse.success("Password changed successfully", null));
    }
}
