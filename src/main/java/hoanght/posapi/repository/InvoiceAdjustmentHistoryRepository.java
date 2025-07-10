package hoanght.posapi.repository;

import hoanght.posapi.entity.InvoiceAdjustmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvoiceAdjustmentHistoryRepository extends JpaRepository<InvoiceAdjustmentHistory, UUID> {
}