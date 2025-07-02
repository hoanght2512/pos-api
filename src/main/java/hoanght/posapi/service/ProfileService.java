package hoanght.posapi.service;

import hoanght.posapi.dto.request.ChangePasswordRequest;
import hoanght.posapi.dto.request.ProfileUpdateRequest;
import hoanght.posapi.dto.response.ProfileResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface ProfileService {
    ProfileResponse getUserProfile(UserDetails userDetails);

    ProfileResponse updateUserProfile(UserDetails userDetails, ProfileUpdateRequest profileUpdateRequest);

    void changePassword(UserDetails userDetails, ChangePasswordRequest changePasswordRequest);
}
