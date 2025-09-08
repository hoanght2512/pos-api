package hoanght.posapi.controller.user;

import hoanght.posapi.assembler.OrderDetailAssembler;
import hoanght.posapi.dto.common.DataResponse;
import hoanght.posapi.dto.order.OrderDetailDto;
import hoanght.posapi.dto.orderdetail.OrderDetailUpdateRequest;
import hoanght.posapi.model.OrderDetail;
import hoanght.posapi.service.OrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user/order-details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;
    private final OrderDetailAssembler orderDetailAssembler;

    @PatchMapping("/{orderDetailId}")
    public ResponseEntity<DataResponse<OrderDetailDto>> updateOrderDetail(@PathVariable Long orderDetailId, @Valid @RequestBody OrderDetailUpdateRequest orderDetailUpdateRequest) {
        OrderDetail orderDetail = orderDetailService.updateOrderDetail(orderDetailId, orderDetailUpdateRequest);
        OrderDetailDto response = orderDetailAssembler.toModel(orderDetail);
        return ResponseEntity.ok(DataResponse.success(response));
    }

    @DeleteMapping("/{orderDetailId}")
    public ResponseEntity<DataResponse<Void>> deleteOrderDetail(@PathVariable Long orderDetailId) {
        orderDetailService.deleteOrderDetail(orderDetailId);
        return ResponseEntity.ok(DataResponse.success("Delete order detail successfully"));
    }
}
