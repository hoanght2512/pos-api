package hoanght.posapi.service;

import hoanght.posapi.dto.orderdetail.OrderDetailUpdateRequest;
import hoanght.posapi.model.OrderDetail;

public interface OrderDetailService {
    OrderDetail updateOrderDetail(Long orderDetailId, OrderDetailUpdateRequest orderDetailUpdateRequest);

    void deleteOrderDetail(Long orderDetailId);
}
