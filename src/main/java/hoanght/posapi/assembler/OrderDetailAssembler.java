package hoanght.posapi.assembler;

import hoanght.posapi.dto.order.OrderDetailDto;
import hoanght.posapi.model.OrderDetail;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class OrderDetailAssembler extends RepresentationModelAssemblerSupport<OrderDetail, OrderDetailDto> {
    private final ModelMapper modelMapper;

    public OrderDetailAssembler(ModelMapper modelMapper) {
        super(OrderDetail.class, OrderDetailDto.class);
        this.modelMapper = modelMapper;
    }

    @Override
    @NonNull
    public OrderDetailDto toModel(@NonNull OrderDetail orderDetail) {
        return modelMapper.map(orderDetail, OrderDetailDto.class);
    }
}
