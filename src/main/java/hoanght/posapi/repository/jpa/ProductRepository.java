package hoanght.posapi.repository.jpa;

import hoanght.posapi.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.inventory WHERE p.deleted = false")
    Page<Product> findAllForAdmin(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.isAvailable = true AND p.deleted = false")
    List<Product> findAllForMenus();

    boolean existsByName(String name);

    boolean existsBySku(String sku);

    boolean existsByNameAndIdNot(String name, Long id);

    boolean existsBySkuAndIdNot(String sku, Long id);

    boolean existsBySlug(String slug);
}