package hoanght.posapi.dto.inventory;

import lombok.Data;

@Data
public class InventoryAdjustmentRequest {
    // Dùng để gán một số lượng tồn kho mới (ví dụ: sau khi kiểm kê)
    private Long newQuantity;

    // Dùng để điều chỉnh cộng/trừ (ví dụ: nhập thêm hàng hoặc ghi nhận hàng hỏng)
    private Long adjustment;
}
