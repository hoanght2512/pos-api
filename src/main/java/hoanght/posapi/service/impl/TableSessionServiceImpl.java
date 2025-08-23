package hoanght.posapi.service.impl;

import hoanght.posapi.assembler.TableSessionAssembler;
import hoanght.posapi.common.OrderStatus;
import hoanght.posapi.common.TableStatus;
import hoanght.posapi.dto.tablesession.AddProductsRequest;
import hoanght.posapi.dto.tablesession.SplitRequest;
import hoanght.posapi.exception.BadRequestException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.model.Order;
import hoanght.posapi.model.OrderDetail;
import hoanght.posapi.model.OrderTable;
import hoanght.posapi.model.Product;
import hoanght.posapi.repository.jpa.OrderRepository;
import hoanght.posapi.repository.jpa.OrderTableRepository;
import hoanght.posapi.repository.jpa.ProductRepository;
import hoanght.posapi.service.InventoryService;
import hoanght.posapi.service.TableSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableSessionServiceImpl implements TableSessionService {
    private final OrderTableRepository orderTableRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final SimpMessagingTemplate messagingTemplate;
    private final TableSessionAssembler tableSessionAssembler;

    @Override
    public Page<OrderTable> getAllTableSessions(Pageable pageable) {
        return orderTableRepository.findAll(pageable);
    }

    @Override
    public OrderTable getTableSessionById(Long tableId) {
        return orderTableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("Table session not found with ID: " + tableId));
    }

    @Override
    @Transactional
    public OrderTable addOrderToTableSession(Long tableId, AddProductsRequest orderRequest) {
        OrderTable orderTable = orderTableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("Table session not found with ID: " + tableId));

        Order order = orderRepository.getOrderByStatusPending(orderTable.getId())
                .orElseGet(() -> {
                    orderTable.setStatus(TableStatus.OCCUPIED);
                    Order newOrder = new Order();
                    newOrder.setStatus(OrderStatus.PENDING);
                    newOrder.setOrderTable(orderTable);
                    return orderRepository.save(newOrder);
                });

        for (AddProductsRequest.Item item : orderRequest.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found with ID: " + item.getProductId()));

            if (product.getCountable()) {
                long stock = product.getInventory().getQuantity();
                if (item.getQuantity() > stock)
                    throw new BadRequestException("Insufficient stock for product: " + product.getName());
                inventoryService.updateProductStock(item.getProductId(), -item.getQuantity());
            }

            OrderDetail orderDetail = order.getOrderDetails().stream()
                    .filter(od -> od.getProduct().getId().equals(product.getId()) &&
                            od.getPriceAtOrder().compareTo(product.getPrice()) == 0 &&
                            Objects.equals(od.getNote(), item.getNote()))
                    .findFirst()
                    .orElseGet(() -> {
                        OrderDetail newDetail = new OrderDetail();
                        newDetail.setOrder(order);
                        newDetail.setProduct(product);
                        newDetail.setPriceAtOrder(product.getPrice());
                        newDetail.setQuantity(0L);
                        newDetail.setNote(item.getNote());
                        order.getOrderDetails().add(newDetail);
                        return newDetail;
                    });

            orderDetail.setQuantity(orderDetail.getQuantity() + item.getQuantity());
        }

        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
        orderTableRepository.save(orderTable);

        messagingTemplate.convertAndSend("/topic/table-sessions", tableSessionAssembler.toModel(orderTable));

        return orderTable;
    }

    @Override
    public OrderTable reserveTableSession(Long tableId) {
        OrderTable orderTable = orderTableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("Table session not found with ID: " + tableId));

        if (orderTable.getStatus() != TableStatus.AVAILABLE)
            throw new BadRequestException("Table is not available for reservation.");

        orderTable.setStatus(TableStatus.RESERVED);
        orderTableRepository.save(orderTable);
        messagingTemplate.convertAndSend("/topic/table-sessions", tableSessionAssembler.toModel(orderTable));

        return orderTable;
    }

    @Override
    public OrderTable changeTableSession(Long fromTableId, Long toTableId) {
        OrderTable fromTable = orderTableRepository.findById(fromTableId)
                .orElseThrow(() -> new NotFoundException("Source table session not found with ID: " + fromTableId));
        OrderTable toTable = orderTableRepository.findById(toTableId)
                .orElseThrow(() -> new NotFoundException("Destination table session not found with ID: " + toTableId));

        if (fromTable.getStatus() != TableStatus.OCCUPIED)
            throw new BadRequestException("Source table is not occupied.");
        if (toTable.getStatus() != TableStatus.AVAILABLE)
            throw new BadRequestException("Destination table is not available.");

        Order order = orderRepository.getOrderByStatusPending(fromTable.getId())
                .orElseThrow(() -> new NotFoundException("No pending order found for source table ID: " + fromTableId));

        order.setOrderTable(toTable);
        orderRepository.save(order);
        fromTable.setStatus(TableStatus.AVAILABLE);
        toTable.setStatus(TableStatus.OCCUPIED);
        orderTableRepository.save(fromTable);
        orderTableRepository.save(toTable);

        messagingTemplate.convertAndSend("/topic/table-sessions", tableSessionAssembler.toModel(fromTable));
        messagingTemplate.convertAndSend("/topic/table-sessions", tableSessionAssembler.toModel(toTable));

        return toTable;
    }

    @Override
    public OrderTable mergeTableSessions(Long fromTableId, Long toTableId) {
        return null;
    }

    @Override
    public OrderTable splitTableSession(Long fromTableId, Long toTableId, SplitRequest splitRequest) {
        return null;
    }

    @Override
    public OrderTable checkoutTableSession(Long tableId) {
        return null;
    }

    @Override
    public OrderTable cancelTableSession(Long tableId) {
        OrderTable orderTable = orderTableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("Table session not found with ID: " + tableId));
        if (orderTable.getStatus() == TableStatus.AVAILABLE)
            throw new BadRequestException("Table is already available.");

        Order order = orderRepository.getOrderByStatusPending(orderTable.getId())
                .orElseThrow(() -> new NotFoundException("No pending order found for table ID: " + tableId));

        for (OrderDetail detail : order.getOrderDetails()) {
            if (detail.getProduct().getCountable())
                inventoryService.updateProductStock(detail.getProduct().getId(), detail.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        orderTable.setStatus(TableStatus.AVAILABLE);
        orderTableRepository.save(orderTable);
        messagingTemplate.convertAndSend("/topic/table-sessions", tableSessionAssembler.toModel(orderTable));

        return orderTable;
    }
}
