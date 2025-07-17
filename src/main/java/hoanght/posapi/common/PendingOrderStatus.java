package hoanght.posapi.common;

public enum PendingOrderStatus {
    WAITED_CONFIRMATION, // Chờ xác nhận từ khách hàng
    IN_PROGRESS,         // Đang được xử lý (có thể là đang chuẩn bị món ăn)
    COMPLETED,           // Đã hoàn thành (có thể đã thanh toán hoặc chưa)
    CANCELLED            // Đã hủy (có thể do khách hàng hoặc do nhà hàng)
}
