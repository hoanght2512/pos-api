package hoanght.posapi.service;

import hoanght.posapi.common.PaymentMethod;
import hoanght.posapi.dto.tablesession.AddProductsRequest;
import hoanght.posapi.dto.tablesession.SplitRequest;
import hoanght.posapi.model.Invoice;
import hoanght.posapi.model.OrderTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TableSessionService {
    Page<OrderTable> getAllTableSessions(Pageable pageable);

    OrderTable getTableSessionById(Long tableId);

    OrderTable addOrderToTableSession(Long tableId, AddProductsRequest orderRequest);

    OrderTable reserveTableSession(Long tableId);

    OrderTable changeTableSession(Long fromTableId, Long toTableId);

    OrderTable mergeTableSessions(Long fromTableId, Long toTableId);

    OrderTable splitTableSession(Long fromTableId, Long toTableId, SplitRequest splitRequest);

    Invoice checkoutTableSession(Long tableId, PaymentMethod paymentMethod);

    OrderTable cancelTableSession(Long tableId);
}
