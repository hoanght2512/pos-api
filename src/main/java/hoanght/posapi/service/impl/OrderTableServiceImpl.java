package hoanght.posapi.service.impl;

import hoanght.posapi.dto.ordertable.OrderTableCreationRequest;
import hoanght.posapi.dto.ordertable.OrderTableUpdateRequest;
import hoanght.posapi.exception.AlreadyExistsException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.model.OrderTable;
import hoanght.posapi.repository.jpa.OrderTableRepository;
import hoanght.posapi.service.OrderTableService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderTableServiceImpl implements OrderTableService {
    private final OrderTableRepository orderTableRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderTable findById(Long tableId) {
        return orderTableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("Table not found with id: " + tableId));
    }

    @Override
    public Page<OrderTable> findAll(Pageable pageable) {
        return orderTableRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public OrderTable create(OrderTableCreationRequest orderTableCreationRequest) {
        if (orderTableRepository.existsByName(orderTableCreationRequest.getName())) {
            throw new AlreadyExistsException("Table with name " + orderTableCreationRequest.getName() + " already exists");
        }
        OrderTable orderTable = modelMapper.map(orderTableCreationRequest, OrderTable.class);
        return orderTableRepository.save(orderTable);
    }

    @Override
    @Transactional
    public OrderTable update(Long tableId, OrderTableUpdateRequest orderTableUpdateRequest) {
        OrderTable existingTable = orderTableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("Table not found with id: " + tableId));

        if (orderTableRepository.existsByNameAndIdNot(orderTableUpdateRequest.getName(), tableId)) {
            throw new AlreadyExistsException("Table with name " + orderTableUpdateRequest.getName() + " already exists");
        }

        existingTable.setName(orderTableUpdateRequest.getName());
        return orderTableRepository.save(existingTable);
    }

    @Override
    @Transactional
    public void delete(Long tableId) {
        OrderTable existingTable = orderTableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("Table not found with id: " + tableId));
        orderTableRepository.delete(existingTable);
    }
}
