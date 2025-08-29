package hoanght.posapi.service.impl;

import hoanght.posapi.common.InvoiceStatus;
import hoanght.posapi.common.OrderStatus;
import hoanght.posapi.common.PaymentMethod;
import hoanght.posapi.common.TableStatus;
import hoanght.posapi.dto.orderdetail.OrderDetailCreationItem;
import hoanght.posapi.dto.orderdetail.OrderDetailSplitItem;
import hoanght.posapi.dto.print.PrintTicket;
import hoanght.posapi.dto.tablesession.AddProductsRequest;
import hoanght.posapi.dto.tablesession.SplitRequest;
import hoanght.posapi.event.PrintTicketEvent;
import hoanght.posapi.event.TableSessionUpdatedEvent;
import hoanght.posapi.exception.BadRequestException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.model.*;
import hoanght.posapi.repository.jpa.InvoiceRepository;
import hoanght.posapi.repository.jpa.OrderRepository;
import hoanght.posapi.repository.jpa.OrderTableRepository;
import hoanght.posapi.repository.jpa.ProductRepository;
import hoanght.posapi.service.InventoryService;
import hoanght.posapi.service.TableSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableSessionServiceImpl implements TableSessionService {
    private final OrderTableRepository orderTableRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;
    private final InventoryService inventoryService;
    private final ApplicationEventPublisher eventPublisher;

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

        Order order = orderRepository.getOrderByStatusInProgressForUpdate(tableId)
                .orElseGet(() -> {
                    orderTable.setStatus(TableStatus.OCCUPIED);
                    Order newOrder = new Order();
                    newOrder.setStatus(OrderStatus.IN_PROGRESS);
                    newOrder.setOrderTable(orderTable);
                    return orderRepository.save(newOrder);
                });

        List<Long> productIds = orderRequest.getItems().stream().map(OrderDetailCreationItem::getProductId).toList();
        Map<Long, Product> productsMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        for (OrderDetailCreationItem item : orderRequest.getItems()) {
            if (!productsMap.containsKey(item.getProductId())) {
                throw new NotFoundException("Product not found with ID: " + item.getProductId());
            }
        }

        List<PrintTicket> printTickets = new ArrayList<>();

        for (OrderDetailCreationItem item : orderRequest.getItems()) {
            Product product = productsMap.get(item.getProductId());

            if (product.getInventory() != null) {
                inventoryService.adjustInventory(product.getId(), item.getQuantity().negate());
            }

            order.addProduct(product, item.getQuantity(), item.getNote());

            PrintTicket ticket = PrintTicket.builder()
                    .content(product.getName())
                    .quantity(item.getQuantity())
                    .note(item.getNote())
                    .build();
            printTickets.add(ticket);
        }

        eventPublisher.publishEvent(new TableSessionUpdatedEvent(this, orderTable));
        eventPublisher.publishEvent(new PrintTicketEvent(this, printTickets));

        return orderTable;
    }

    @Override
    @Transactional
    public OrderTable reserveTableSession(Long tableId) {
        OrderTable orderTable = orderTableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("Table session not found with ID: " + tableId));

        if (orderTable.getStatus() != TableStatus.AVAILABLE)
            throw new BadRequestException("Table is not available for reservation.");

        orderTable.setStatus(TableStatus.RESERVED);

        eventPublisher.publishEvent(new TableSessionUpdatedEvent(this, orderTable));

        return orderTable;
    }

    @Override
    @Transactional
    public OrderTable changeTableSession(Long fromTableId, Long toTableId) {
        OrderTable fromTable = orderTableRepository.findById(fromTableId)
                .orElseThrow(() -> new NotFoundException("Source table session not found with ID: " + fromTableId));
        OrderTable toTable = orderTableRepository.findById(toTableId)
                .orElseThrow(() -> new NotFoundException("Destination table session not found with ID: " + toTableId));

        if (fromTable.getStatus() != TableStatus.OCCUPIED)
            throw new BadRequestException("Source table is not occupied.");
        if (toTable.getStatus() != TableStatus.AVAILABLE)
            throw new BadRequestException("Destination table is not available.");

        Order order = orderRepository.getOrderByStatusInProgressForUpdate(fromTable.getId())
                .orElseThrow(() -> new NotFoundException("No pending order found for source table ID: " + fromTableId));

        order.setOrderTable(toTable);
        fromTable.setStatus(TableStatus.AVAILABLE);
        toTable.setStatus(TableStatus.OCCUPIED);

        eventPublisher.publishEvent(new TableSessionUpdatedEvent(this, fromTable));
        eventPublisher.publishEvent(new TableSessionUpdatedEvent(this, toTable));

        return toTable;
    }

    @Override
    @Transactional
    public OrderTable mergeTableSessions(Long fromTableId, Long toTableId) {
        OrderTable fromTable = orderTableRepository.findById(fromTableId)
                .orElseThrow(() -> new NotFoundException("Source table session not found with ID: " + fromTableId));
        OrderTable toTable = orderTableRepository.findById(toTableId)
                .orElseThrow(() -> new NotFoundException("Destination table session not found with ID: " + toTableId));

        if (fromTable.getStatus() != TableStatus.OCCUPIED)
            throw new BadRequestException("Source table is not occupied.");
        if (toTable.getStatus() != TableStatus.OCCUPIED)
            throw new BadRequestException("Destination table is not occupied.");

        Order fromOrder = orderRepository.getOrderByStatusInProgressForUpdate(fromTable.getId())
                .orElseThrow(() -> new NotFoundException("No pending order found for source table ID: " + fromTableId));
        Order toOrder = orderRepository.getOrderByStatusInProgressForUpdate(toTable.getId())
                .orElseThrow(() -> new NotFoundException("No pending order found for destination table ID: " + toTableId));

        fromOrder.getOrderDetails().forEach(d -> toOrder.addProduct(d.getProduct(), d.getQuantity(), d.getNote()));

        orderRepository.delete(fromOrder);
        fromTable.setStatus(TableStatus.AVAILABLE);

        eventPublisher.publishEvent(new TableSessionUpdatedEvent(this, fromTable));
        eventPublisher.publishEvent(new TableSessionUpdatedEvent(this, toTable));

        return toTable;
    }

    @Override
    @Transactional
    public OrderTable splitTableSession(Long fromTableId, Long toTableId, SplitRequest splitRequest) {
        OrderTable fromTable = orderTableRepository.findById(fromTableId)
                .orElseThrow(() -> new NotFoundException("Source table session not found with ID: " + fromTableId));
        OrderTable toTable = orderTableRepository.findById(toTableId)
                .orElseThrow(() -> new NotFoundException("Destination table session not found with ID: " + toTableId));

        if (fromTable.getStatus() != TableStatus.OCCUPIED)
            throw new BadRequestException("Source table is not occupied.");

        Order fromOrder = orderRepository.getOrderByStatusInProgressForUpdate(fromTable.getId())
                .orElseThrow(() -> new NotFoundException("No pending order found for source table ID: " + fromTableId));

        Order toOrder = orderRepository.getOrderByStatusInProgressForUpdate(toTable.getId())
                .orElseGet(() -> {
                    toTable.setStatus(TableStatus.OCCUPIED);
                    Order newOrder = new Order();
                    newOrder.setStatus(OrderStatus.IN_PROGRESS);
                    newOrder.setOrderTable(toTable);
                    return orderRepository.save(newOrder);
                });

        Map<Long, OrderDetail> fromOrderDetailsMap = fromOrder.getOrderDetails().stream()
                .collect(Collectors.toMap(OrderDetail::getId, od -> od));

        for (OrderDetailSplitItem item : splitRequest.getDetails()) {
            OrderDetail fromDetail = fromOrderDetailsMap.get(item.getOrderDetailId());
            if (fromDetail == null)
                throw new NotFoundException("Order detail not found with ID: " + item.getOrderDetailId());

            if (item.getQuantity().compareTo(fromDetail.getQuantity()) > 0)
                throw new BadRequestException("Cannot move more than existing quantity for detail ID: " + item.getOrderDetailId());

            toOrder.addProduct(fromDetail.getProduct(), item.getQuantity(), fromDetail.getNote());
            fromDetail.setQuantity(fromDetail.getQuantity().subtract(item.getQuantity()));
        }

        fromOrder.getOrderDetails().removeIf(od -> od.getQuantity().compareTo(BigDecimal.ZERO) == 0);

        if (fromOrder.getOrderDetails().isEmpty()) {
            fromTable.setStatus(TableStatus.AVAILABLE);
            orderRepository.delete(fromOrder);
        }

        eventPublisher.publishEvent(new TableSessionUpdatedEvent(this, fromTable));
        eventPublisher.publishEvent(new TableSessionUpdatedEvent(this, toTable));

        return toTable;
    }

    @Override
    @Transactional
    public Invoice checkoutTableSession(Long tableId, PaymentMethod paymentMethod) {
        OrderTable orderTable = orderTableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("Table session not found with ID: " + tableId));

        if (orderTable.getStatus() != TableStatus.OCCUPIED)
            throw new BadRequestException("Table is not occupied.");

        Order order = orderRepository.getOrderByStatusInProgressForUpdate(orderTable.getId())
                .orElseThrow(() -> new NotFoundException("No pending order found for table ID: " + tableId));

        if (order.getOrderDetails().isEmpty())
            throw new BadRequestException("Cannot checkout an order with no items.");

        Invoice invoice = Invoice.builder()
                .order(order)
                .orderTable(orderTable)
                .paymentMethod(paymentMethod)
                .status(InvoiceStatus.PAID)
                .totalAmount(order.getTotalAmount())
                .build();

        invoiceRepository.save(invoice);

        order.setStatus(OrderStatus.COMPLETED);
        orderTable.setStatus(TableStatus.AVAILABLE);

        eventPublisher.publishEvent(new TableSessionUpdatedEvent(this, orderTable));

        return invoice;
    }

    @Override
    @Transactional
    public OrderTable cancelTableSession(Long tableId) {
        OrderTable orderTable = orderTableRepository.findById(tableId)
                .orElseThrow(() -> new NotFoundException("Table session not found with ID: " + tableId));
        if (orderTable.getStatus() == TableStatus.AVAILABLE)
            throw new BadRequestException("Table is already available.");

        Order order = orderRepository.getOrderByStatusInProgressForUpdate(orderTable.getId())
                .orElseThrow(() -> new NotFoundException("No pending order found for table ID: " + tableId));

        for (OrderDetail detail : order.getOrderDetails()) {
            if (detail.getProduct().getCountable())
                inventoryService.adjustInventory(detail.getProduct().getId(), detail.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderTable.setStatus(TableStatus.AVAILABLE);

        eventPublisher.publishEvent(new TableSessionUpdatedEvent(this, orderTable));

        return orderTable;
    }
}
