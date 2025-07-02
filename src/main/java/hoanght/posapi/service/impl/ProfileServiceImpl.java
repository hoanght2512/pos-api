package hoanght.posapi.service.impl;

import hoanght.posapi.dto.request.ChangePasswordRequest;
import hoanght.posapi.dto.request.ProfileUpdateRequest;
import hoanght.posapi.dto.response.ProfileResponse;
import hoanght.posapi.entity.User;
import hoanght.posapi.repository.UserRepository;
import hoanght.posapi.security.CustomUserDetails;
import hoanght.posapi.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ProfileResponse getUserProfile(UserDetails userDetails) {
        User user = ((CustomUserDetails) userDetails).getUser();
        return modelMapper.map(user, ProfileResponse.class);
    }

    @Override
    public ProfileResponse updateUserProfile(UserDetails userDetails, ProfileUpdateRequest profileUpdateRequest) {
        User user = ((CustomUserDetails) userDetails).getUser();
        if (profileUpdateRequest.getFullName() != null) {
            user.setFullName(profileUpdateRequest.getFullName());
        }
        // Verify email
        if (profileUpdateRequest.getEmail() != null) {
            user.setEmail(profileUpdateRequest.getEmail());
        }
        userRepository.save(user);
        return modelMapper.map(user, ProfileResponse.class);
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
