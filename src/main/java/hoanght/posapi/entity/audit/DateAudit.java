package hoanght.posapi.entity.audit;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class DateAudit {
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
}
