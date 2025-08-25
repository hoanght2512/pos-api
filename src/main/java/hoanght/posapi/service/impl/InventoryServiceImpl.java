package hoanght.posapi.service.impl;

import hoanght.posapi.exception.BadRequestException;
import hoanght.posapi.repository.jpa.InventoryRepository;
import hoanght.posapi.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public void adjustInventory(Long productId, Long quantityChange) {
        log.info("Product Lock: {}", productId);
        inventoryRepository.findByProductIdWithPessimisticLock(productId).ifPresent(inventory -> {
            long newQuantity = inventory.getQuantity() + quantityChange;
            if (newQuantity < 0) {
                throw new BadRequestException("Insufficient inventory for product ID: " + productId);
            }
            inventory.setQuantity(newQuantity);
            inventoryRepository.save(inventory);
        });
    }
}
