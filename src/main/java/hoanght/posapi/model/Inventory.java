package hoanght.posapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import hoanght.posapi.model.audit.DateAudit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

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

    @Column(name = "quantity", nullable = false)
    private Long quantity;
}