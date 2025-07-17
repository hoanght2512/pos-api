package hoanght.posapi.repository.jpa;

import hoanght.posapi.model.InvoiceAdjustmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceAdjustmentHistoryRepository extends JpaRepository<InvoiceAdjustmentHistory, Long> {
}