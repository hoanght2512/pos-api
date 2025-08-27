package hoanght.posapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import hoanght.posapi.model.audit.DateAudit;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "inventories")
public class Inventory extends DateAudit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @OneToOne
    @JoinColumn(name = "product_id", unique = true, nullable = false)
    @JsonBackReference
    private Product product;

    @Digits(integer = 10, fraction = 3)
    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;
}