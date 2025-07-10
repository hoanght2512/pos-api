package hoanght.posapi.common;

public enum InvoiceStatus {
    PAID, // Đã thanh toán đầy đủ
    REFUNDED, // Đã hoàn tiền
    ADJUSTED, // Đã điều chỉnh
    PARTIAL_REFUND // Hoàn tiền một phần
}
