package hoanght.posapi.service;

import hoanght.posapi.dto.user.UserResponse;
import hoanght.posapi.dto.user.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponse> findAll(Pageable pageable);

    UserResponse findUserById(Long userId);

    UserResponse updateUser(Long userId, UserUpdateRequest userUpdateRequest);

    void deleteUser(Long userId);
}
