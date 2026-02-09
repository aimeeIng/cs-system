package com.cs.cs.service;

package com.bank.queue.service;

import com.bank.queue.dto.QueueStats;
import com.bank.queue.model.Ticket;
import com.bank.queue.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueueService {

    private final TicketRepository ticketRepository;
    private final NotificationService notificationService;

    /**
     * Update queue positions for all waiting tickets
     */
    @Transactional
    public void updateQueuePositions(Long serviceId) {
        List<Ticket> waitingTickets = ticketRepository
                .findByServiceIdAndStatusOrderByCreatedAtAsc(serviceId, "WAITING");

        for (int i = 0; i < waitingTickets.size(); i++) {
            Ticket ticket = waitingTickets.get(i);
            ticket.setQueuePosition(i + 1);

            // Notify if they're next (position 1)
            if (i == 0) {
                notificationService.sendAlmostYourTurnSMS(ticket, 1);
            }
        }

        ticketRepository.saveAll(waitingTickets);
    }

    /**
     * Get queue statistics for a service
     */
    public QueueStats getQueueStats(Long serviceId) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);

        Long waitingCount = ticketRepository.countByServiceIdAndStatus(serviceId, "WAITING");
        Long servingCount = ticketRepository.countByServiceIdAndStatus(serviceId, "SERVING");

        List<Ticket> completedToday = ticketRepository.findTicketsCreatedToday(startOfDay)
                .stream()
                .filter(t -> t.getService().getId().equals(serviceId))
                .filter(t -> "COMPLETED".equals(t.getStatus()))
                .toList();

        Double avgWaitTime = completedToday.stream()
                .filter(t -> t.getCalledAt() != null)
                .mapToLong(t -> Duration.between(t.getCreatedAt(), t.getCalledAt()).toMinutes())
                .average()
                .orElse(0.0);

        return QueueStats.builder()
                .waitingCount(waitingCount.intValue())
                .servingCount(servingCount.intValue())
                .completedToday(completedToday.size())
                .averageWaitTimeMinutes(avgWaitTime)
                .build();
    }

    /**
     * Expire old tickets (older than 2 hours)
     */
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    @Transactional
    public void expireOldTickets() {
        LocalDateTime expiryTime = LocalDateTime.now().minusHours(2);
        List<Ticket> expiredTickets = ticketRepository.findExpiredTickets(expiryTime);

        for (Ticket ticket : expiredTickets) {
            ticket.setStatus("EXPIRED");
            log.info("Expired ticket: {}", ticket.getTicketNumber());
        }

        if (!expiredTickets.isEmpty()) {
            ticketRepository.saveAll(expiredTickets);
        }
    }
}