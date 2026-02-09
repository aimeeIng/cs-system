package com.cs.cs.controller;

package com.bank.queue.controller;

import com.bank.queue.dto.CreateTicketRequest;
import com.bank.queue.dto.TicketResponse;
import com.bank.queue.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed.origins}")
public class TicketController {

    private final TicketService ticketService;

    /**
     * Create a new ticket
     * POST /api/tickets
     */
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        TicketResponse response = ticketService.createTicket(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get ticket by number
     * GET /api/tickets/{ticketNumber}
     */
    @GetMapping("/{ticketNumber}")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable String ticketNumber) {
        TicketResponse response = ticketService.getTicket(ticketNumber);
        return ResponseEntity.ok(response);
    }
}