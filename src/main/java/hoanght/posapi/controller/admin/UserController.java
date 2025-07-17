package hoanght.posapi.controller.admin;

import hoanght.posapi.dto.common.DataResponse;
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
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<DataResponse<PagedModel<UserResponse>>> findAllUsers(Pageable pageable) {
        Page<UserResponse> users = userService.findAll(pageable);
        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(users.getSize(), users.getNumber(), users.getTotalElements());
        PagedModel<UserResponse> pagedModel = PagedModel.of(users.getContent(), pageMetadata, linkTo(methodOn(UserController.class).findAllUsers(pageable)).withSelfRel());
        return ResponseEntity.ok(DataResponse.success("Users retrieved successfully", pagedModel));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Find user by ID", description = "Retrieve a user by their unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    public ResponseEntity<DataResponse<UserResponse>> findUserById(@PathVariable Long userId) {
        UserResponse userResponse = userService.findUserById(userId);
        return ResponseEntity.ok(DataResponse.success("User retrieved successfully", userResponse));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Update user", description = "Update an existing user's details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    public ResponseEntity<DataResponse<UserResponse>> updateUser(@PathVariable Long userId, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        UserResponse updatedUser = userService.updateUser(userId, userUpdateRequest);
        return ResponseEntity.ok(DataResponse.success("User updated successfully", updatedUser));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user", description = "Delete a user by their unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("@customSecurityExpression.isAdminOrSelf(#userId)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
