package hoanght.posapi.service;

import hoanght.posapi.dto.UserRequest;
import hoanght.posapi.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    Page<UserResponse> findAll(Pageable pageable);

    UserResponse findUserById(UUID id);

    UserResponse createUser(UserRequest userRequest);

    UserResponse updateUser(UUID id, UserRequest userRequest);

    void deleteUser(UUID id);
}
