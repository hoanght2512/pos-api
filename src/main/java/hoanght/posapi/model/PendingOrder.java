package hoanght.posapi.model;

import hoanght.posapi.common.PendingOrderStatus;
import hoanght.posapi.model.audit.UserDateAudit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "pending_orders")
public class PendingOrder extends UserDateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_table_id", nullable = false)
    private OrderTable orderTable;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PendingOrderStatus status;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "pendingOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PendingOrderItem> pendingOrderItems = new LinkedHashSet<>();
}