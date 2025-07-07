package hoanght.posapi.service;

import hoanght.posapi.dto.user.UserResponse;
import hoanght.posapi.dto.user.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    Page<UserResponse> findAll(Pageable pageable);

    UserResponse findUserById(UUID userId);

    UserResponse updateUser(UUID userId, UserUpdateRequest userUpdateRequest);

    void deleteUser(UUID userId);
}
