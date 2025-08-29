package hoanght.posapi.listener;

import hoanght.posapi.event.PrintTicketEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PrintTicketEventListener {
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePrintTicketEvent(PrintTicketEvent event) {
        messagingTemplate.convertAndSend("/topic/print-tickets", event);
    }
}
