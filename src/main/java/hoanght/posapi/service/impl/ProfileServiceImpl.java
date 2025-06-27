package hoanght.posapi.service.impl;

import hoanght.posapi.dto.ChangePasswordRequest;
import hoanght.posapi.dto.UserProfileRequest;
import hoanght.posapi.dto.UserProfileResponse;
import hoanght.posapi.entity.User;
import hoanght.posapi.repository.UserRepository;
import hoanght.posapi.security.CustomUserDetails;
import hoanght.posapi.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserProfileResponse getUserProfile(UserDetails userDetails) {
        User user = ((CustomUserDetails) userDetails).getUser();
        return modelMapper.map(user, UserProfileResponse.class);
    }

    @Override
    public UserProfileResponse updateUserProfile(UserDetails userDetails, UserProfileRequest userProfileRequest) {
        User user = ((CustomUserDetails) userDetails).getUser();
        user.setFullName(userProfileRequest.getFullName());
        user.setEmail(userProfileRequest.getEmail());
        userRepository.save(user);
        return modelMapper.map(user, UserProfileResponse.class);
    }

    @Override
    public void changePassword(UserDetails userDetails, ChangePasswordRequest changePasswordRequest) {
        User user = ((CustomUserDetails) userDetails).getUser();
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }
}
