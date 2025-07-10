package hoanght.posapi.common;

public enum TableStatus {
    AVAILABLE,  // Sẵn sàng
    OCCUPIED,   // Đang có khách (đã có order đang hoạt động)
    RESERVED,   // Đã đặt trước nhưng chưa có khách
    CLEANING    // Đang dọn dẹp
}
