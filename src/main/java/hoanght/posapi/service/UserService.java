package hoanght.posapi.service;

import hoanght.posapi.dto.user.UserCreationRequest;
import hoanght.posapi.dto.user.UserUpdateRequest;
import hoanght.posapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<User> findAll(Pageable pageable);

    User findById(Long userId);

    User createUser(UserCreationRequest userCreationRequest);

    User updateUser(Long userId, UserUpdateRequest userUpdateRequest);

    void delete(Long userId);
}
