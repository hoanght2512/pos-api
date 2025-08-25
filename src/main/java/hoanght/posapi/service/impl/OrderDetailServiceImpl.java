package hoanght.posapi.service.impl;

import hoanght.posapi.common.OrderStatus;
import hoanght.posapi.dto.orderdetail.OrderDetailUpdateRequest;
import hoanght.posapi.exception.BadRequestException;
import hoanght.posapi.exception.NotFoundException;
import hoanght.posapi.model.Order;
import hoanght.posapi.model.OrderDetail;
import hoanght.posapi.model.Product;
import hoanght.posapi.repository.jpa.OrderDetailRepository;
import hoanght.posapi.service.InventoryService;
import hoanght.posapi.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final InventoryService inventoryService;

    private OrderDetail findAndValidateOrderDetail(Long orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId).orElseThrow(() -> new NotFoundException("Order detail not found with id: " + orderDetailId));

        if (!orderDetail.getOrder().getStatus().equals(OrderStatus.PENDING))
            throw new BadRequestException("Cannot modify an order that is not in PENDING status.");
        return orderDetail;
    }

    @Override
    @Transactional
    public OrderDetail updateOrderDetail(Long orderDetailId, OrderDetailUpdateRequest request) {
        OrderDetail orderDetail = findAndValidateOrderDetail(orderDetailId);
        Product product = orderDetail.getProduct();
        long oldQuantity = orderDetail.getQuantity();
        Long newQuantity = request.getQuantity();

        long quantityDifference = (newQuantity != null ? newQuantity : oldQuantity) - oldQuantity;

        BigDecimal newPrice = request.getPrice();
        boolean isReducing = (quantityDifference < 0) || (newPrice != null && newPrice.compareTo(orderDetail.getPriceAtOrder()) < 0);
        if (isReducing && (request.getReason() == null || request.getReason().isBlank()))
            throw new BadRequestException("Reason is required when reducing quantity or price.");

        if (product.getCountable() && quantityDifference != 0) {
            inventoryService.adjustInventory(product.getId(), -quantityDifference);
        }

        Optional.ofNullable(request.getQuantity()).ifPresent(orderDetail::setQuantity);
        Optional.ofNullable(request.getPrice()).ifPresent(orderDetail::setPriceAtOrder);
        Optional.ofNullable(request.getNote()).ifPresent(orderDetail::setNote);

        return orderDetailRepository.save(orderDetail);
    }

    @Override
    @Transactional
    public void deleteOrderDetail(Long orderDetailId) {
        OrderDetail orderDetail = findAndValidateOrderDetail(orderDetailId);
        Order order = orderDetail.getOrder();
        Product product = orderDetail.getProduct();

        if (product.getCountable())
            inventoryService.adjustInventory(product.getId(), orderDetail.getQuantity());

        order.getOrderDetails().remove(orderDetail);

        orderDetailRepository.delete(orderDetail);
    }
}
