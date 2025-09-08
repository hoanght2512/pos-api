package hoanght.posapi.event;

import hoanght.posapi.model.OrderTable;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TableSessionUpdatedEvent extends ApplicationEvent {
    private final OrderTable orderTable;

    public TableSessionUpdatedEvent(Object source, OrderTable orderTable) {
        super(source);
        this.orderTable = orderTable;
    }
}
