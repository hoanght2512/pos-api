package hoanght.posapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "product_options")
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price_adjustment", nullable = false)
    private Double priceAdjustment;

    @Column(name = "is_additive", nullable = false)
    private boolean isAdditive;

    @OneToMany(mappedBy = "productOption", orphanRemoval = true)
    private Set<OrderItemOption> orderItemOptions = new LinkedHashSet<>();
}