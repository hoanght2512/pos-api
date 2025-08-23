package hoanght.posapi.service.impl;

import hoanght.posapi.exception.BadRequestException;
import hoanght.posapi.model.Inventory;
import hoanght.posapi.repository.jpa.InventoryRepository;
import hoanght.posapi.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final RedisLockRegistry redisLockRegistry;
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#productId"),
            @CacheEvict(value = "products", allEntries = true)
    })
    public void updateProductStock(long productId, long quantityChange) {
        Lock lock = null;
        try {
            lock = redisLockRegistry.obtain("product_stock_update_" + productId);
            boolean acquired = lock.tryLock(500, TimeUnit.MILLISECONDS);

            if (acquired) {
                Inventory inventory = inventoryRepository.findByProductId(productId)
                        .orElseThrow(() -> new BadRequestException("Product not found: " + productId));
                long newStock = inventory.getQuantity() + quantityChange;
                if (newStock < 0) {
                    throw new BadRequestException("Insufficient stock for product: " + productId);
                }
                inventory.setQuantity(newStock);
                inventoryRepository.save(inventory);
            } else {
                throw new BadRequestException("Product is being updated by another process, please try again later.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Lock acquisition interrupted for product: {}: {}", productId, e.getMessage());
            throw new RuntimeException("Inventory update interrupted for product: " + productId, e); // Ném lại RuntimeException
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating stock for product {}: {}", productId, e.getMessage(), e);
            throw new RuntimeException("Failed to update stock for product: " + productId, e);
        } finally {
            if (lock != null) {
                try {
                    lock.unlock();
                } catch (IllegalStateException e) {
                    log.error("Attempted to unlock an expired/unowned lock for product: {}: {}", productId, e.getMessage());
                }
            }
        }
    }
}
