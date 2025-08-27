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

    private boolean isReduction(OrderDetailUpdateRequest request, OrderDetail orderDetail, BigDecimal quantityDifference) {
        BigDecimal newPrice = request.getPrice();
        boolean isReducing = quantityDifference.compareTo(BigDecimal.ZERO) < 0
                || (newPrice != null && newPrice.compareTo(orderDetail.getPrice()) < 0);

        return isReducing && (request.getReason() == null || request.getReason().isBlank());
    }

    @Override
    @Transactional
    public OrderDetail updateOrderDetail(Long orderDetailId, OrderDetailUpdateRequest request) {
        OrderDetail orderDetail = findAndValidateOrderDetail(orderDetailId);
        Product product = orderDetail.getProduct();

        BigDecimal oldQuantity = orderDetail.getQuantity();
        BigDecimal newQuantity = Optional.ofNullable(request.getQuantity()).orElse(oldQuantity);
        BigDecimal quantityDifference = newQuantity.subtract(oldQuantity);

        if (isReduction(request, orderDetail, quantityDifference)) {
            throw new BadRequestException("A reason must be provided when reducing quantity or price.");
        }

        if (product.getCountable() && quantityDifference.compareTo(BigDecimal.ZERO) != 0) {
            inventoryService.adjustInventory(product.getId(), quantityDifference.negate());
        }

        Optional.ofNullable(request.getQuantity()).ifPresent(orderDetail::setQuantity);
        Optional.ofNullable(request.getPrice()).ifPresent(orderDetail::setPrice);
        Optional.ofNullable(request.getNote()).ifPresent(orderDetail::setNote);

        return orderDetailRepository.save(orderDetail);
    }

    @Override
    @Transactional
    public void deleteOrderDetail(Long orderDetailId) {
        OrderDetail orderDetail = findAndValidateOrderDetail(orderDetailId);
        Product product = orderDetail.getProduct();

        if (product.getCountable())
            inventoryService.adjustInventory(product.getId(), orderDetail.getQuantity());

        orderDetailRepository.delete(orderDetail);
    }
}
