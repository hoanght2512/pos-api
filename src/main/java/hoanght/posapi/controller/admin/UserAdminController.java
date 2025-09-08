package hoanght.posapi.controller.admin;

import hoanght.posapi.assembler.UserAssembler;
import hoanght.posapi.dto.common.DataResponse;
import hoanght.posapi.dto.user.UserCreationRequest;
import hoanght.posapi.dto.user.UserResponse;
import hoanght.posapi.dto.user.UserUpdateRequest;
import hoanght.posapi.model.User;
import hoanght.posapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/users")
public class UserAdminController {
    private final UserService userService;
    private final UserAssembler userAssembler;

    @GetMapping
    public ResponseEntity<DataResponse<?>> findAllUsers(Pageable pageable, PagedResourcesAssembler<User> assembler) {
        Page<User> users = userService.findAll(pageable);
        PagedModel<UserResponse> response = assembler.toModel(users, userAssembler);
        return ResponseEntity.ok(DataResponse.success("Users retrieved successfully", response));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<DataResponse<?>> findUserById(@PathVariable Long userId) {
        User user = userService.findById(userId);
        UserResponse response = userAssembler.toModel(user);
        return ResponseEntity.ok(DataResponse.success("User found successfully", response));
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody UserCreationRequest userCreationRequest) {
        User newUser = userService.createUser(userCreationRequest);
        return ResponseEntity.created(linkTo(methodOn(UserAdminController.class).findUserById(newUser.getId())).toUri()).build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<DataResponse<?>> updateUser(@PathVariable Long userId, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        User updatedUser = userService.updateUser(userId, userUpdateRequest);
        UserResponse response = userAssembler.toModel(updatedUser);
        return ResponseEntity.ok(DataResponse.success("User updated successfully", response));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("!@customSecurityExpression.isYourself(#userId)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
