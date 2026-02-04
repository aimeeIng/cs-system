package com.cs.cs.controller;

import com.cs.cs.model.*;
import com.cs.cs.service.TicketService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/tickets")
public class PublicTicketController {

    private final TicketService ticketService;

    public PublicTicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public Ticket createTicket(
            @RequestParam String phone,
            @RequestParam ServiceType serviceType
    ) {
        return ticketService.createTicket(phone, serviceType);
    }
}
