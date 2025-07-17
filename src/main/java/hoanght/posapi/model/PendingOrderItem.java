package hoanght.posapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "pending_order_items")
public class PendingOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pending_order_id", nullable = false)
    private PendingOrder pendingOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price_at_order", nullable = false)
    private BigDecimal priceAtOrder;

    @OneToMany(mappedBy = "pendingOrderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PendingOrderItemOption> options = new LinkedHashSet<>();
}