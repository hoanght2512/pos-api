package hoanght.posapi.model;

import hoanght.posapi.model.audit.UserDateAudit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "invoice_adjustment_histories")
public class InvoiceAdjustmentHistory extends UserDateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price_at_adjustment", nullable = false)
    private BigDecimal priceAtAdjustment;

    @Column(name = "quantity_at_adjustment", nullable = false)
    private Integer quantityAtAdjustment;
}