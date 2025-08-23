package hoanght.posapi.repository.jpa;

import hoanght.posapi.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.orderTable.id = ?1 AND o.status = 'PENDING'")
    Optional<Order> getOrderByStatusPending(Long tableId);
}