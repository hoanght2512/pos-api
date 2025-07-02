package hoanght.posapi.controller.impl;

import hoanght.posapi.controller.IUserController;
import hoanght.posapi.dto.request.UserCreationRequest;
import hoanght.posapi.dto.request.UserUpdateRequest;
import hoanght.posapi.dto.response.DataResponse;
import hoanght.posapi.dto.response.UserResponse;
import hoanght.posapi.entity.User;
import hoanght.posapi.exception.BadRequestException;
import hoanght.posapi.security.CustomUserDetails;
import hoanght.posapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class UserController implements IUserController {
    private final UserService userService;

    @Override
    public ResponseEntity<DataResponse<PagedModel<EntityModel<UserResponse>>>> findAllUsers(Pageable pageable, PagedResourcesAssembler<UserResponse> assembler) {
        Page<UserResponse> users = userService.findAll(pageable);
        PagedModel<EntityModel<UserResponse>> pagedModel = assembler.toModel(users, user ->
                EntityModel.of(user,
                        linkTo(methodOn(UserController.class).findUserById(user.getId())).withSelfRel(),
                        linkTo(methodOn(UserController.class).deleteUser(null, user.getId())).withRel("delete"),
                        linkTo(methodOn(UserController.class).updateUser(user.getId(), null)).withRel("update")
                )
        );
        pagedModel.add(linkTo(methodOn(UserController.class).createUser(null)).withRel("create"));
        DataResponse<PagedModel<EntityModel<UserResponse>>> response = DataResponse.success("Fetched all users successfully", pagedModel);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<DataResponse<EntityModel<UserResponse>>> findUserById(UUID id) {
        UserResponse userResponse = userService.findUserById(id);
        EntityModel<UserResponse> entityModel = EntityModel.of(userResponse,
                linkTo(methodOn(UserController.class).findUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(null, id)).withRel("delete"),
                linkTo(methodOn(UserController.class).findAllUsers(Pageable.unpaged(), null)).withRel("list-users")
        );
        DataResponse<EntityModel<UserResponse>> response = DataResponse.success("User found successfully", entityModel);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<DataResponse<EntityModel<UserResponse>>> createUser(UserCreationRequest createUserRequest) {
        UserResponse createdUser = userService.createUser(createUserRequest);
        EntityModel<UserResponse> entityModel = EntityModel.of(createdUser,
                linkTo(methodOn(UserController.class).findUserById(createdUser.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).updateUser(createdUser.getId(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(null, createdUser.getId())).withRel("delete"),
                linkTo(methodOn(UserController.class).findAllUsers(Pageable.unpaged(), null)).withRel("list-users")
        );
        DataResponse<EntityModel<UserResponse>> response = DataResponse.success("User created successfully", entityModel);
        return ResponseEntity.status(201).body(response);
    }

    @Override
    public ResponseEntity<DataResponse<EntityModel<UserResponse>>> updateUser(UUID id, UserUpdateRequest createUserRequest) {
        UserResponse updatedUser = userService.updateUser(id, createUserRequest);
        EntityModel<UserResponse> entityModel = EntityModel.of(updatedUser,
                linkTo(methodOn(UserController.class).findUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(null, id)).withRel("delete"),
                linkTo(methodOn(UserController.class).findAllUsers(Pageable.unpaged(), null)).withRel("list-users")
        );
        DataResponse<EntityModel<UserResponse>> response = DataResponse.success("User updated successfully", entityModel);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<DataResponse<Void>> deleteUser(UserDetails userDetails, UUID id) {
        User user = ((CustomUserDetails) userDetails).getUser();
        if (user.getId().equals(id)) throw new BadRequestException("Cannot delete yourself");
        userService.deleteUser(id);
        DataResponse<Void> response = DataResponse.success("User deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}
