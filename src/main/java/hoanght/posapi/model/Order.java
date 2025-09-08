package hoanght.posapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import hoanght.posapi.common.OrderStatus;
import hoanght.posapi.model.audit.UserDateAudit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order extends UserDateAudit implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_table_id")
    @JsonBackReference
    private OrderTable orderTable;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @Transient
    public BigDecimal getTotalAmount() {
        return orderDetails.stream()
                .map(detail -> detail.getPrice().multiply(detail.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transient
    public void addProduct(Product product, BigDecimal quantity, String note) {
        OrderDetail detail = this.getOrderDetails().stream()
                .filter(od -> od.getProduct().getId().equals(product.getId()) &&
                        od.getPrice().compareTo(product.getPrice()) == 0 &&
                        Objects.equals(od.getNote(), note))
                .findFirst()
                .orElseGet(() -> {
                    OrderDetail newDetail = OrderDetail.builder()
                            .order(this)
                            .product(product)
                            .price(product.getPrice())
                            .quantity(BigDecimal.ZERO)
                            .note(note)
                            .build();
                    this.orderDetails.add(newDetail);
                    return newDetail;
                });
        detail.setQuantity(detail.getQuantity().add(quantity));
    }
}