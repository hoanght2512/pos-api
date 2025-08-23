package hoanght.posapi.assembler;

import hoanght.posapi.controller.admin.OrderTableAdminController;
import hoanght.posapi.dto.ordertable.OrderTableResponse;
import hoanght.posapi.model.OrderTable;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderTableAssembler extends RepresentationModelAssemblerSupport<OrderTable, OrderTableResponse> {
    private final ModelMapper modelMapper;

    public OrderTableAssembler(ModelMapper modelMapper) {
        super(OrderTableAdminController.class, OrderTableResponse.class);
        this.modelMapper = modelMapper;
    }

    @Override
    @NonNull
    public OrderTableResponse toModel(@NonNull OrderTable table) {
        OrderTableResponse response = modelMapper.map(table, OrderTableResponse.class);
        response.add(linkTo(methodOn(OrderTableAdminController.class).findTableById(table.getId())).withSelfRel());
        response.add(linkTo(methodOn(OrderTableAdminController.class).updateTable(table.getId(), null)).withRel("update"));
        response.add(linkTo(methodOn(OrderTableAdminController.class).deleteTable(table.getId())).withRel("delete"));
        return response;
    }
}