package com.cs.cs.controller;

package com.bank.queue.controller;

import com.bank.queue.dto.DisplayUpdate;
import com.bank.queue.model.Ticket;
import com.bank.queue.model.Window;
import com.bank.queue.repository.TicketRepository;
import com.bank.queue.repository.WindowRepository;
import com.bank.queue.service.NotificationService;
import com.bank.queue.service.QueueService;
import com.bank.queue.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/teller")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "${cors.allowed.origins}")
public class TellerController {

    private final WindowRepository windowRepository;
    private final TicketRepository ticketRepository;
    private final NotificationService notificationService;
    private final WebSocketService webSocketService;
    private final QueueService queueService;

    /**
     * Call next customer
     * POST /api/teller/call-next?windowId=1
     */
    @PostMapping("/call-next")
    @Transactional
    public ResponseEntity<?> callNext(@RequestParam Long windowId) {
        // Get window
        Window window = windowRepository.findById(windowId)
                .orElseThrow(() -> new RuntimeException("Window not found"));

        // Get next ticket for this service
        Optional<Ticket> nextTicketOpt = ticketRepository
                .findFirstByServiceIdAndStatusOrderByCreatedAtAsc(
                        window.getService().getId(),
                        "WAITING"
                );

        if (nextTicketOpt.isEmpty()) {
            return ResponseEntity.ok().body("No customers waiting");
        }

        Ticket nextTicket = nextTicketOpt.get();

        // Update ticket status
        nextTicket.setStatus("CALLED");
        nextTicket.setWindow(window);
        nextTicket.setCalledAt(LocalDateTime.now());
        ticketRepository.save(nextTicket);

        // Update window status
        window.setCurrentTicketId(nextTicket.getId());
        window.setStatus("SERVING");
        windowRepository.save(window);

        // Send SMS to customer
        notificationService.sendYoureTurnSMS(nextTicket);

        // Get next ticket in queue for display
        Optional<Ticket> upcomingTicketOpt = ticketRepository
                .findFirstByServiceIdAndStatusOrderByCreatedAtAsc(
                        window.getService().getId(),
                        "WAITING"
                );

        // Build display update
        DisplayUpdate displayUpdate = DisplayUpdate.builder()
                .current(DisplayUpdate.CurrentTicket.builder()
                        .ticketNumber(nextTicket.getTicketNumber())
                        .windowNumber(window.getWindowNumber())
                        .serviceName(window.getService().getName())
                        .build())
                .next(upcomingTicketOpt.map(upcoming ->
                        DisplayUpdate.NextTicket.builder()
                                .ticketNumber(upcoming.getTicketNumber())
                                .serviceName(upcoming.getService().getName())
                                .build()
                ).orElse(null))
                .build();

        // Broadcast to display screens
        webSocketService.broadcastDisplayUpdate(displayUpdate);

        // Update queue positions
        queueService.updateQueuePositions(window.getService().getId());

        log.info("Called ticket {} to window {}", nextTicket.getTicketNumber(), window.getWindowNumber());

        return ResponseEntity.ok(displayUpdate);
    }

    /**
     * Mark customer as served and complete
     * POST /api/teller/complete?windowId=1
     */
    @PostMapping("/complete")
    @Transactional
    public ResponseEntity<?> completeService(@RequestParam Long windowId) {
        Window window = windowRepository.findById(windowId)
                .orElseThrow(() -> new RuntimeException("Window not found"));

        if (window.getCurrentTicketId() == null) {
            return ResponseEntity.badRequest().body("No active ticket at this window");
        }

        Ticket ticket = ticketRepository.findById(window.getCurrentTicketId())
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Mark ticket as completed
        ticket.setStatus("COMPLETED");
        ticket.setCompletedAt(LocalDateTime.now());
        ticketRepository.save(ticket);

        // Reset window
        window.setCurrentTicketId(null);
        window.setStatus("IDLE");
        windowRepository.save(window);

        log.info("Completed service for ticket {}", ticket.getTicketNumber());

        return ResponseEntity.ok("Service completed");
    }
}