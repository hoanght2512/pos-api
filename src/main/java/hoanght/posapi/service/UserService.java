package hoanght.posapi.service;

import hoanght.posapi.dto.request.UserCreationRequest;
import hoanght.posapi.dto.request.UserUpdateRequest;
import hoanght.posapi.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    Page<UserResponse> findAll(Pageable pageable);

    UserResponse findUserById(UUID id);

    UserResponse createUser(UserCreationRequest userCreationRequest);

    UserResponse updateUser(UUID id, UserUpdateRequest userUpdateRequest);

    void deleteUser(UUID id);
}
