package hoanght.posapi.model;

import hoanght.posapi.common.InvoiceStatus;
import hoanght.posapi.common.PaymentMethod;
import hoanght.posapi.model.audit.UserDateAudit;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "invoices")
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Digits(integer = 10, fraction = 0)
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
}