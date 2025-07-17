package hoanght.posapi.model.audit;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.util.UUID;

public class UserDateAudit extends DateAudit {
    @CreatedBy
    private UUID createdBy;
    @LastModifiedBy
    private UUID updatedBy;
}
