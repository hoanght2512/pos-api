package hoanght.posapi.service;

public interface InventoryService {
    void adjustInventory(Long productId, Long quantityChange);
}
