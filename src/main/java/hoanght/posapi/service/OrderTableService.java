package hoanght.posapi.service;

import hoanght.posapi.dto.ordertable.OrderTableCreationRequest;
import hoanght.posapi.dto.ordertable.OrderTableUpdateRequest;
import hoanght.posapi.model.OrderTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderTableService {
    Page<OrderTable> findAll(Pageable pageable);

    OrderTable findById(Long tableId);

    OrderTable create(OrderTableCreationRequest orderTableCreationRequest);

    OrderTable update(Long tableId, OrderTableUpdateRequest orderTableUpdateRequest);

    void delete(Long tableId);
}
