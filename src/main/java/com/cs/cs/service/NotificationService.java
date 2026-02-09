package com.cs.cs.service;

import com.cs.cs.model.Notification;
import com.cs.cs.model.Ticket;
import com.cs.cs.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * MOCK SMS SENDER - Logs to console instead of sending real SMS
     */
    public void sendSMS(String toPhoneNumber, String messageText, Ticket ticket) {
        try {
            // Log instead of sending real SMS
            log.info("📱 MOCK SMS to {}: {}", toPhoneNumber, messageText);

            // Save notification record
            Notification notification = new Notification();
            notification.setTicket(ticket);
            notification.setPhoneNumber(toPhoneNumber);
            notification.setMessage(messageText);
            notification.setStatus("SENT");
            notificationRepository.save(notification);

        } catch (Exception e) {
            log.error("Failed to log SMS to {}: {}", toPhoneNumber, e.getMessage());

            Notification notification = new Notification();
            notification.setTicket(ticket);
            notification.setPhoneNumber(toPhoneNumber);
            notification.setMessage(messageText);
            notification.setStatus("FAILED");
            notification.setErrorMessage(e.getMessage());
            notificationRepository.save(notification);
        }
    }

    public void sendTicketCreatedSMS(Ticket ticket) {
        String message = String.format(
                "Your ticket is %s. You are number %d in the queue for %s service.",
                ticket.getTicketNumber(),
                ticket.getQueuePosition(),
                ticket.getService().getName()
        );
        sendSMS(ticket.getPhoneNumber(), message, ticket);
    }

    public void sendYoureTurnSMS(Ticket ticket) {
        String message = String.format(
                "Your turn! Please proceed to %s Window %d. Ticket: %s",
                ticket.getService().getName(),
                ticket.getWindow().getWindowNumber(),
                ticket.getTicketNumber()
        );
        sendSMS(ticket.getPhoneNumber(), message, ticket);
    }

    public void sendAlmostYourTurnSMS(Ticket ticket, int position) {
        String message = String.format(
                "You're next! %d customer(s) ahead. Ticket: %s",
                position,
                ticket.getTicketNumber()
        );
        sendSMS(ticket.getPhoneNumber(), message, ticket);
    }
}