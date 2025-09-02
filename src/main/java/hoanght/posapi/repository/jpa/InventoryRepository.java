package hoanght.posapi.repository.jpa;

import hoanght.posapi.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = i.quantity + :quantityChange WHERE i.id = :productId AND (i.quantity + :quantityChange) >= 0")
    int adjustInventory(@Param("productId") Long productId, @Param("quantityChange") BigDecimal quantityChange);
}