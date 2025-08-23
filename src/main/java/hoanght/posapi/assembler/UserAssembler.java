package hoanght.posapi.assembler;

import hoanght.posapi.controller.admin.UserAdminController;
import hoanght.posapi.dto.user.UserResponse;
import hoanght.posapi.model.User;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserAssembler extends RepresentationModelAssemblerSupport<User, UserResponse> {
    private final ModelMapper modelMapper;

    public UserAssembler(ModelMapper modelMapper) {
        super(User.class, UserResponse.class);
        this.modelMapper = modelMapper;
    }

    @Override
    @NonNull
    public UserResponse toModel(@NonNull User user) {
        UserResponse response = modelMapper.map(user, UserResponse.class);
        response.add(linkTo(methodOn(UserAdminController.class).findUserById(user.getId())).withSelfRel());
        response.add(linkTo(methodOn(UserAdminController.class).findAllUsers(Pageable.unpaged(), null)).withRel("all"));
        response.add(linkTo(methodOn(UserAdminController.class).updateUser(user.getId(), null)).withRel("update"));
        response.add(linkTo(methodOn(UserAdminController.class).deleteUser(user.getId())).withRel("delete"));
        return response;
    }
}
