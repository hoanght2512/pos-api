package hoanght.posapi.repository;

import hoanght.posapi.entity.OrderTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderTableRepository extends JpaRepository<OrderTable, UUID> {
}