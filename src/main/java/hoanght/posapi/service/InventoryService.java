package hoanght.posapi.service;

import java.math.BigDecimal;

public interface InventoryService {
    void adjustInventory(Long productId, BigDecimal quantityChange);
}
