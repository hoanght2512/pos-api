package hoanght.posapi.event;

import hoanght.posapi.dto.print.PrintTicket;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class PrintTicketEvent extends ApplicationEvent {
    private final List<PrintTicket> printTickets;

    public PrintTicketEvent(Object source, List<PrintTicket> printTickets) {
        super(source);
        this.printTickets = printTickets;
    }
}
