package hoanght.posapi.service.impl;

import hoanght.posapi.dto.UserRequest;
import hoanght.posapi.dto.UserResponse;
import hoanght.posapi.entity.User;
import hoanght.posapi.exception.ResourceNotFoundException;
import hoanght.posapi.repository.UserRepository;
import hoanght.posapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public UserResponse createUser(UserRequest userRequest) {
        User newUser = new User();
        newUser.setUsername(userRequest.getUsername());
        newUser.setPassword(userRequest.getPassword());
        newUser.setEmail(userRequest.getEmail());
        newUser.setFullName(userRequest.getFullName());
        return modelMapper.map(userRepository.save(newUser), UserResponse.class);
    }

    @Override
    public UserResponse updateUser(UUID id, UserRequest userRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        existingUser.setUsername(userRequest.getUsername());
        existingUser.setPassword(userRequest.getPassword());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setFullName(userRequest.getFullName());

        return modelMapper.map(userRepository.save(existingUser), UserResponse.class);
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }
}
