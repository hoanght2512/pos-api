package hoanght.posapi.service.impl;

import hoanght.posapi.common.Role;
import hoanght.posapi.dto.user.UserCreationRequest;
import hoanght.posapi.dto.user.UserUpdateRequest;
import hoanght.posapi.exception.AlreadyExistsException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.model.User;
import hoanght.posapi.repository.jpa.UserRepository;
import hoanght.posapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public User createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AlreadyExistsException("Username already exists: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Email already exists: " + request.getEmail());
        }

        Set<Role> defaultRoles = new HashSet<>();
        defaultRoles.add(Role.ROLE_USER);
        defaultRoles.addAll(request.getRoles());

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setFullName(request.getFullName());
        newUser.setEmail(request.getEmail());
        newUser.setRoles(defaultRoles);

        return userRepository.save(newUser);
    }

    @Override
    public User updateUser(Long userId, UserUpdateRequest request) {
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Optional.ofNullable(request.getFullName()).ifPresent(existingUser::setFullName);
        Optional.ofNullable(request.getPassword()).ifPresent(pw -> existingUser.setPassword(passwordEncoder.encode(pw)));
        Optional.ofNullable(request.getUsername()).ifPresent(username -> {
            if (userRepository.existsByUsername(username) && !existingUser.getUsername().equals(username)) {
                throw new AlreadyExistsException("Username already exists: " + username);
            }
            existingUser.setUsername(username);
        });
        Optional.ofNullable(request.getIsEnabled()).ifPresent(existingUser::setEnabled);
        Optional.ofNullable(request.getRoles()).ifPresent(existingUser::setRoles);

        return userRepository.save(existingUser);
    }

    @Override
    public void delete(Long userId) {
        User deleteUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        userRepository.delete(deleteUser);
    }
}
