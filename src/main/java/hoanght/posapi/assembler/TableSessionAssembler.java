package hoanght.posapi.assembler;

import hoanght.posapi.controller.user.TableSessionController;
import hoanght.posapi.dto.order.OrderResponse;
import hoanght.posapi.dto.tablesession.TableSessionResponse;
import hoanght.posapi.model.OrderTable;
import hoanght.posapi.repository.jpa.OrderRepository;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TableSessionAssembler extends RepresentationModelAssemblerSupport<OrderTable, TableSessionResponse> {
    private final ModelMapper modelMapper;
    private final OrderRepository orderRepository;

    public TableSessionAssembler(ModelMapper modelMapper, OrderRepository orderRepository) {
        super(TableSessionController.class, TableSessionResponse.class);
        this.modelMapper = modelMapper;
        this.orderRepository = orderRepository;
    }

    @Override
    @NonNull
    public TableSessionResponse toModel(@NonNull OrderTable table) {
        TableSessionResponse response = modelMapper.map(table, TableSessionResponse.class);
        OrderResponse order = orderRepository.getOrderByStatusPending(table.getId())
                .map(o -> modelMapper.map(o, OrderResponse.class))
                .orElse(null);
        response.setOrder(order);
        response.add(linkTo(methodOn(TableSessionController.class).getTableSessionById(table.getId())).withSelfRel());
        response.add(linkTo(methodOn(TableSessionController.class).getAllTableSessions(Pageable.unpaged(), null)).withRel("all"));
        response.add(linkTo(methodOn(TableSessionController.class).addOrdersToTableSession(table.getId(), null)).withRel("add-orders"));
        response.add(linkTo(methodOn(TableSessionController.class).reserveTableSession(table.getId())).withRel("reserve"));
        response.add(linkTo(methodOn(TableSessionController.class).changeTableSession(table.getId(), null)).withRel("change-table"));
        response.add(linkTo(methodOn(TableSessionController.class).mergeTableSessions(table.getId(), null)).withRel("merge-tables"));
        response.add(linkTo(methodOn(TableSessionController.class).splitTableSession(table.getId(), null, null)).withRel("split-table"));
        response.add(linkTo(methodOn(TableSessionController.class).checkoutTableSession(table.getId())).withRel("checkout"));
        response.add(linkTo(methodOn(TableSessionController.class).cancelTableSession(table.getId())).withRel("cancel"));
        return response;
    }
}
