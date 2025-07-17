package hoanght.posapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "pending_order_item_options")
public class PendingOrderItemOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pending_order_item_id", nullable = false)
    private PendingOrderItem pendingOrderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_id", nullable = false)
    private ProductOption productOption;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price_at_order", nullable = false)
    private BigDecimal priceAtOrder;
}