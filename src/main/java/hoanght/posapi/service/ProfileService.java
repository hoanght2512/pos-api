package hoanght.posapi.service;

import hoanght.posapi.dto.ChangePasswordRequest;
import hoanght.posapi.dto.UserProfileRequest;
import hoanght.posapi.dto.UserProfileResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface ProfileService {
    UserProfileResponse getUserProfile(UserDetails userDetails);

    UserProfileResponse updateUserProfile(UserDetails userDetails, UserProfileRequest userProfileRequest);

    void changePassword(UserDetails userDetails, ChangePasswordRequest changePasswordRequest);
}
