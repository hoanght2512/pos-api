package hoanght.posapi.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import hoanght.posapi.common.TableStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tables")
@SQLDelete(sql = "UPDATE tables SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class OrderTable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TableStatus status = TableStatus.AVAILABLE;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @OneToMany(mappedBy = "orderTable")
    @JsonManagedReference
    private Set<Order> orders = new LinkedHashSet<>();
}