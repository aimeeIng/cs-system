package com.cs.cs.service;

import com.cs.cs.dto.CreateTicketRequest;
import com.cs.cs.dto.TicketResponse;
import com.cs.cs.model.Service;
import com.cs.cs.model.Ticket;
import com.cs.cs.repository.ServiceRepository;
import com.cs.cs.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@org.springframework.stereotype.Service
@Slf4j
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ServiceRepository serviceRepository;
    private final NotificationService notificationService;
    private final WebSocketService webSocketService;

    /**
     * Create a new ticket for a customer
     */
    @Transactional
    public TicketResponse createTicket(CreateTicketRequest request) {
        // Get service
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // Generate ticket number
        String ticketNumber = generateTicketNumber(service);

        // Calculate queue position
        Long waitingCount = ticketRepository.countByServiceIdAndStatus(service.getId(), "WAITING");
        Integer queuePosition = waitingCount.intValue() + 1;

        // Create ticket
        Ticket ticket = new Ticket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setPhoneNumber(request.getPhoneNumber());
        ticket.setService(service);
        ticket.setQueuePosition(queuePosition);
        ticket.setStatus("WAITING");

        ticket = ticketRepository.save(ticket);

        // Send SMS notification
        notificationService.sendTicketCreatedSMS(ticket);

        // Broadcast queue update
        webSocketService.broadcastQueueUpdate(service.getName(), queuePosition);

        log.info("Created ticket: {} for service: {}", ticketNumber, service.getName());

        return mapToResponse(ticket);
    }

    /**
     * Generate unique ticket number (e.g., D-001, W-042)
     */
    private String generateTicketNumber(Service service) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = ticketRepository.countByServiceIdAndStatus(service.getId(), "WAITING") +
                ticketRepository.countByServiceIdAndStatus(service.getId(), "CALLED") +
                ticketRepository.countByServiceIdAndStatus(service.getId(), "SERVING") + 1;

        return String.format("%s-%s-%03d", service.getPrefix(), dateStr, count);
    }

    /**
     * Get ticket details
     */
    public TicketResponse getTicket(String ticketNumber) {
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        return mapToResponse(ticket);
    }

    /**
     * Map Ticket entity to TicketResponse DTO
     */
    private TicketResponse mapToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .phoneNumber(ticket.getPhoneNumber())
                .serviceName(ticket.getService().getName())
                .status(ticket.getStatus())
                .queuePosition(ticket.getQueuePosition())
                .windowNumber(ticket.getWindow() != null ? ticket.getWindow().getWindowNumber() : null)
                .createdAt(ticket.getCreatedAt())
                .build();
    }
}
