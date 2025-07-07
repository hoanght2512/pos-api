package hoanght.posapi.service.impl;

import hoanght.posapi.dto.user.UserResponse;
import hoanght.posapi.dto.user.UserUpdateRequest;
import hoanght.posapi.entity.User;
import hoanght.posapi.exception.AlreadyExistsException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.repository.UserRepository;
import hoanght.posapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<UserResponse> findAll(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(user -> modelMapper.map(user, UserResponse.class));
    }

    @Override
    public UserResponse findUserById(UUID id) {
        return userRepository.findById(id).map(user -> modelMapper.map(user, UserResponse.class)).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public UserResponse updateUser(UUID userId, UserUpdateRequest userUpdateRequest) {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Optional.ofNullable(userUpdateRequest.getFullName()).ifPresent(existingUser::setFullName);
        Optional.ofNullable(userUpdateRequest.getPassword()).ifPresent(pw -> existingUser.setPassword(passwordEncoder.encode(pw)));
        Optional.ofNullable(userUpdateRequest.getUsername()).ifPresent(username -> {
            if (userRepository.existsByUsername(username) && !existingUser.getUsername().equals(username)) {
                throw new AlreadyExistsException("Username already exists: " + username);
            }
            existingUser.setUsername(username);
        });
        Optional.ofNullable(userUpdateRequest.getIsEnabled()).ifPresent(existingUser::setEnabled);
        Optional.ofNullable(userUpdateRequest.getRoles()).ifPresent(existingUser::setRoles);

        return modelMapper.map(userRepository.save(existingUser), UserResponse.class);
    }

    @Override
    public void deleteUser(UUID userId) {
        User deleteUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        userRepository.delete(deleteUser);
    }
}
