package hoanght.posapi.model;

import hoanght.posapi.common.InvoiceStatus;
import hoanght.posapi.common.PaymentMethod;
import hoanght.posapi.model.audit.UserDateAudit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "invoices")
public class Invoice extends UserDateAudit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_table_id", nullable = false)
    private OrderTable orderTable;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    public void calculateTotalAmount() {
        if (order != null && !order.getOrderDetails().isEmpty()) {
            this.totalAmount = order.getOrderDetails().stream()
                    .map(od -> od.getPriceAtOrder().multiply(BigDecimal.valueOf(od.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return;
        }
        this.totalAmount = BigDecimal.ZERO;
    }
}