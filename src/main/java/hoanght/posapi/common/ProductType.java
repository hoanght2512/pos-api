package hoanght.posapi.common;

public enum ProductType {
    COUNTABLE,      // Sản phẩm đếm được (cái, lon, chai)
    DECIMAL_QUANTITY, // Sản phẩm bán theo đơn vị thập phân (kg, lít)
    TIMED_SERVICE   // Dịch vụ tính theo giờ
}
