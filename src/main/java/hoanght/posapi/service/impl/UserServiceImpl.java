package hoanght.posapi.service.impl;

import hoanght.posapi.controller.admin.UserController;
import hoanght.posapi.dto.user.UserResponse;
import hoanght.posapi.dto.user.UserUpdateRequest;
import hoanght.posapi.exception.AlreadyExistsException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.model.User;
import hoanght.posapi.repository.jpa.UserRepository;
import hoanght.posapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private UserResponse mapToResponse(User user) {
        UserResponse response = modelMapper.map(user, UserResponse.class);
        response.add(linkTo(methodOn(UserController.class).findUserById(user.getId())).withSelfRel());
        response.add(linkTo(methodOn(UserController.class).updateUser(user.getId(), null)).withRel("update"));
        response.add(linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel("delete"));
        return response;
    }

    @Override
    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public UserResponse findUserById(Long id) {
        UserResponse response = userRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new NotFoundException("User not found"));
        response.add(linkTo(methodOn(UserController.class).findAllUsers(Pageable.unpaged())).withRel("all"));
        return response;
    }

    @Override
    public UserResponse updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
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

        return mapToResponse(userRepository.save(existingUser));
    }

    @Override
    public void deleteUser(Long userId) {
        User deleteUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        userRepository.delete(deleteUser);
    }
}
