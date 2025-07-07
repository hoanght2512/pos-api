package hoanght.posapi.controller;

import hoanght.posapi.dto.DataResponse;
import hoanght.posapi.dto.user.UserResponse;
import hoanght.posapi.dto.user.UserUpdateRequest;
import hoanght.posapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/users")
@Tag(name = "User", description = "User management APIs")
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Find all users", description = "Retrieve a paginated list of all users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @Parameters({
            @Parameter(description = "Page number for pagination"),
            @Parameter(description = "Page size for pagination")
    })
    public ResponseEntity<DataResponse<PagedModel<EntityModel<UserResponse>>>> findAllUsers(Pageable pageable, PagedResourcesAssembler<UserResponse> assembler) {
        Page<UserResponse> users = userService.findAll(pageable);
        PagedModel<EntityModel<UserResponse>> pagedModel = assembler.toModel(users, user -> EntityModel.of(user,
                linkTo(methodOn(UserController.class).findUserById(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel("delete"),
                linkTo(methodOn(UserController.class).updateUser(user.getId(), null)).withRel("update")));
        DataResponse<PagedModel<EntityModel<UserResponse>>> response = DataResponse.success("Fetched all users successfully", pagedModel);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Find user by ID", description = "Retrieve a user by their unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    public ResponseEntity<DataResponse<EntityModel<UserResponse>>> findUserById(@PathVariable UUID userId) {
        UserResponse userResponse = userService.findUserById(userId);
        EntityModel<UserResponse> entityModel = EntityModel.of(userResponse,
                linkTo(methodOn(UserController.class).findUserById(userId)).withSelfRel(),
                linkTo(methodOn(UserController.class).updateUser(userId, null)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(userId)).withRel("delete"),
                linkTo(methodOn(UserController.class).findAllUsers(Pageable.unpaged(), null)).withRel("list-users"));
        DataResponse<EntityModel<UserResponse>> response = DataResponse.success("User retrieved successfully", entityModel);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user", description = "Update an existing user's details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    public ResponseEntity<DataResponse<EntityModel<UserResponse>>> updateUser(@PathVariable UUID userId, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        UserResponse updatedUser = userService.updateUser(userId, userUpdateRequest);
        EntityModel<UserResponse> entityModel = EntityModel.of(updatedUser,
                linkTo(methodOn(UserController.class).findUserById(userId)).withSelfRel(),
                linkTo(methodOn(UserController.class).updateUser(userId, null)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(userId)).withRel("delete"),
                linkTo(methodOn(UserController.class).findAllUsers(Pageable.unpaged(), null)).withRel("list-users")
        );
        DataResponse<EntityModel<UserResponse>> response = DataResponse.success("User updated successfully", entityModel);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user", description = "Delete a user by their unique identifier")
    @ApiResponses({@ApiResponse(responseCode = "204", description = "User deleted successfully"), @ApiResponse(responseCode = "404", description = "User not found"), @ApiResponse(responseCode = "403", description = "Access forbidden")})
    @PreAuthorize("@customSecurityExpression.isAdminOrSelf(#userId)")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
