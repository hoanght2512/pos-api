package hoanght.posapi.controller;

import hoanght.posapi.dto.request.UserCreationRequest;
import hoanght.posapi.dto.request.UserUpdateRequest;
import hoanght.posapi.dto.response.DataResponse;
import hoanght.posapi.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/admin/users")
@Tag(name = "User", description = "User management APIs")
public interface IUserController {
    @GetMapping
    @Operation(summary = "Find all users", description = "Retrieve a paginated list of all users")
    ResponseEntity<DataResponse<PagedModel<EntityModel<UserResponse>>>> findAllUsers(Pageable pageable, PagedResourcesAssembler<UserResponse> assembler);

    @GetMapping("/{id}")
    @Operation(summary = "Find user by ID", description = "Retrieve a user by their unique identifier")
    ResponseEntity<DataResponse<EntityModel<UserResponse>>> findUserById(@PathVariable UUID id);

    @PostMapping
    @Operation(summary = "Create a new user", description = "Create a new user with the provided details")
    ResponseEntity<DataResponse<EntityModel<UserResponse>>> createUser(@Valid @RequestBody UserCreationRequest createUserRequest);

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user's details")
    ResponseEntity<DataResponse<EntityModel<UserResponse>>> updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest createUserRequest);

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by their unique identifier")
    ResponseEntity<DataResponse<Void>> deleteUser(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id);
}
