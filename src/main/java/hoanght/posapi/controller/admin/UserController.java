package hoanght.posapi.controller.admin;

import hoanght.posapi.dto.DataResponse;
import hoanght.posapi.dto.UserRequest;
import hoanght.posapi.dto.UserResponse;
import hoanght.posapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<DataResponse<PagedModel<EntityModel<UserResponse>>>> findAllUsers(Pageable pageable, PagedResourcesAssembler<UserResponse> assembler) {
        Page<UserResponse> users = userService.findAll(pageable);
        PagedModel<EntityModel<UserResponse>> pagedModel = assembler.toModel(users, user ->
                EntityModel.of(user,
                        linkTo(methodOn(UserController.class).findUserById(UUID.fromString(user.getId()))).withSelfRel(),
                        linkTo(methodOn(UserController.class).deleteUser(UUID.fromString(user.getId()))).withRel("delete"),
                        linkTo(methodOn(UserController.class).updateUser(UUID.fromString(user.getId()), new UserRequest())).withRel("update")
                )
        );
        pagedModel.add(linkTo(methodOn(UserController.class).createUser(null)).withRel("create"));
        DataResponse<PagedModel<EntityModel<UserResponse>>> response = DataResponse.success("Fetched all users successfully", pagedModel);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<EntityModel<UserResponse>>> findUserById(@PathVariable UUID id) {
        UserResponse userResponse = userService.findUserById(id);
        EntityModel<UserResponse> entityModel = EntityModel.of(userResponse,
                linkTo(methodOn(UserController.class).findUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).updateUser(id, new UserRequest())).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete"),
                linkTo(methodOn(UserController.class).findAllUsers(Pageable.unpaged(), null)).withRel("list-users")
        );
        DataResponse<EntityModel<UserResponse>> response = DataResponse.success("User found successfully", entityModel);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DataResponse<EntityModel<UserResponse>>> createUser(@RequestBody UserRequest userRequest) {
        UserResponse createdUser = userService.createUser(userRequest);
        EntityModel<UserResponse> entityModel = EntityModel.of(createdUser,
                linkTo(methodOn(UserController.class).findUserById(UUID.fromString(createdUser.getId()))).withSelfRel(),
                linkTo(methodOn(UserController.class).updateUser(UUID.fromString(createdUser.getId()), new UserRequest())).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(UUID.fromString(createdUser.getId()))).withRel("delete"),
                linkTo(methodOn(UserController.class).findAllUsers(Pageable.unpaged(), null)).withRel("list-users")
        );
        DataResponse<EntityModel<UserResponse>> response = DataResponse.success("User created successfully", entityModel);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataResponse<EntityModel<UserResponse>>> updateUser(@PathVariable UUID id, @RequestBody UserRequest userRequest) {
        UserResponse updatedUser = userService.updateUser(id, userRequest);
        EntityModel<UserResponse> entityModel = EntityModel.of(updatedUser,
                linkTo(methodOn(UserController.class).findUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).updateUser(id, userRequest)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete"),
                linkTo(methodOn(UserController.class).findAllUsers(Pageable.unpaged(), null)).withRel("list-users")
        );
        DataResponse<EntityModel<UserResponse>> response = DataResponse.success("User updated successfully", entityModel);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DataResponse<Void>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        DataResponse<Void> response = DataResponse.success("User deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}
