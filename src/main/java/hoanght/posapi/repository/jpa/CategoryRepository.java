package hoanght.posapi.repository.jpa;

import hoanght.posapi.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    boolean existsByIdAndProductsIsNotEmpty(Long id);
}