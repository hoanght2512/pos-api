package hoanght.posapi.repository.jpa;

import hoanght.posapi.model.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT o FROM Order o WHERE o.orderTable.id = ?1 AND o.status = 'IN_PROGRESS'")
    Optional<Order> getOrderByStatusInProgressForUpdate(Long tableId);

    @Query("SELECT o FROM Order o WHERE o.orderTable.id = ?1 AND o.status = 'IN_PROGRESS'")
    Optional<Order> getOrderByStatusInProgress(Long tableId);
}