package hoanght.posapi.service.impl;

import hoanght.posapi.exception.BadRequestException;
import hoanght.posapi.repository.jpa.InventoryRepository;
import hoanght.posapi.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;

    @Override
    public void adjustInventory(Long productId, BigDecimal quantityChange) {
        if(inventoryRepository.adjustInventory(productId, quantityChange) == 0) {
            throw new BadRequestException("Insufficient inventory for product ID: " + productId);
        }
    }
}
