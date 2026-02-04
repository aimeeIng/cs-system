package com.cs.cs.repository;

import com.cs.cs.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    int countByServiceTypeAndStatus(ServiceType type, TicketStatus status);

    Optional<Ticket> findFirstByServiceTypeAndStatusOrderByCreatedAt(
            ServiceType type,
            TicketStatus status
    );
}
