package hoanght.posapi.service;

public interface InventoryService {
    void updateProductStock(long productId, long quantityChange);
}
