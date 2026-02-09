package com.cs.cs.repository;

import com.cs.cs.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Count waiting tickets for a service
    Long countByServiceIdAndStatus(Long serviceId, String status);

    // Get next ticket in queue for a service
    Optional<Ticket> findFirstByServiceIdAndStatusOrderByCreatedAtAsc(Long serviceId, String status);

    // Get all tickets for a service with status
    List<Ticket> findByServiceIdAndStatusOrderByCreatedAtAsc(Long serviceId, String status);

    // Find ticket by number
    Optional<Ticket> findByTicketNumber(String ticketNumber);

    // Get tickets created today
    @Query("SELECT t FROM Ticket t WHERE t.createdAt >= :startOfDay")
    List<Ticket> findTicketsCreatedToday(LocalDateTime startOfDay);

    // Get expired waiting tickets
    @Query("SELECT t FROM Ticket t WHERE t.status = 'WAITING' AND t.createdAt < :expiryTime")
    List<Ticket> findExpiredTickets(LocalDateTime expiryTime);
}