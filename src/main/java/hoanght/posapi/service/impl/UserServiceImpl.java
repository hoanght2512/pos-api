package hoanght.posapi.service.impl;

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

import java.util.Optional;

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
    public User update(Long userId, UserUpdateRequest userUpdateRequest) {
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

        return userRepository.save(existingUser);
    }

    @Override
    public void delete(Long userId) {
        User deleteUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        userRepository.delete(deleteUser);
    }
}
