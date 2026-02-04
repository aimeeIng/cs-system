package com.cs.cs.service;

import com.cs.cs.model.*;
import com.cs.cs.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public Ticket createTicket(String phoneNumber, ServiceType type) {

        int position =
                ticketRepository.countByServiceTypeAndStatus(type, TicketStatus.WAITING) + 1;

        Ticket ticket = new Ticket();
        ticket.setPhoneNumber(phoneNumber);
        ticket.setServiceType(type);
        ticket.setPosition(position);
        ticket.setTicketNumber(generateTicketNumber(type, position));

        return ticketRepository.save(ticket);
    }

    private String generateTicketNumber(ServiceType type, int pos) {
        return type.name().charAt(0) + "-" + String.format("%03d", pos);
    }
}
