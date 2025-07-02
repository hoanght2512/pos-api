package hoanght.posapi.service.impl;

import hoanght.posapi.dto.request.UserCreationRequest;
import hoanght.posapi.dto.request.UserUpdateRequest;
import hoanght.posapi.dto.response.UserResponse;
import hoanght.posapi.entity.User;
import hoanght.posapi.exception.ResourceNotFoundException;
import hoanght.posapi.repository.UserRepository;
import hoanght.posapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<UserResponse> findAll(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(user -> modelMapper.map(user, UserResponse.class));
    }

    @Override
    public UserResponse findUserById(UUID id) {
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UserResponse.class))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public UserResponse createUser(UserCreationRequest userCreationRequest) {
        User newUser = new User();
        newUser.setUsername(userCreationRequest.getUsername());
        newUser.setPassword(userCreationRequest.getPassword());
        newUser.setFullName(userCreationRequest.getFullName());
        return modelMapper.map(userRepository.save(newUser), UserResponse.class);
    }

    // Cần xem lại
    @Override
    public UserResponse updateUser(UUID id, UserUpdateRequest userUpdateRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        Optional.ofNullable(userUpdateRequest.getFullName())
                .ifPresent(existingUser::setFullName);

        return modelMapper.map(userRepository.save(existingUser), UserResponse.class);
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }
}
