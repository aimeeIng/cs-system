package com.cs.cs.controller;

import com.cs.cs.model.*;
import com.cs.cs.repository.TicketRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teller")
public class TellerController {

    private final TicketRepository ticketRepository;

    public TellerController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @PostMapping("/next")
    public Ticket callNext(
            @RequestParam ServiceType serviceType,
            @RequestParam int window
    ) {
        Ticket ticket = ticketRepository
                .findFirstByServiceTypeAndStatusOrderByCreatedAt(serviceType, TicketStatus.WAITING)
                .orElseThrow();

        ticket.setStatus(TicketStatus.SERVING);
        ticket.setWindowNumber(window);

        return ticketRepository.save(ticket);
    }
}
