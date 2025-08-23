package hoanght.posapi.repository.jpa;

import hoanght.posapi.model.OrderTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderTableRepository extends JpaRepository<OrderTable, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}