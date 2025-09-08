package hoanght.posapi.listener;

import hoanght.posapi.assembler.TableSessionAssembler;
import hoanght.posapi.event.TableSessionUpdatedEvent;
import hoanght.posapi.model.OrderTable;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TableSessionEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final TableSessionAssembler tableSessionAssembler;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTableSessionUpdate(TableSessionUpdatedEvent event) {
        OrderTable orderTable = event.getOrderTable();
        messagingTemplate.convertAndSend("/topic/table-sessions", tableSessionAssembler.toModel(orderTable));
    }
}
