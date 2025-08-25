package hoanght.posapi.repository.jpa;

import hoanght.posapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    boolean existsBySku(String sku);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsBySkuAndIdNot(String sku, Long id);
}