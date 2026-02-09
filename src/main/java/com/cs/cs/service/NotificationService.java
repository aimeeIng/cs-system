package com.cs.cs.service;

package com.bank.queue.service;

import com.bank.queue.model.Notification;
import com.bank.queue.model.Ticket;
import com.bank.queue.repository.NotificationRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void sendSMS(String toPhoneNumber, String messageText, Ticket ticket) {
        try {
            Twilio.init(accountSid, authToken);

            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(fromPhoneNumber),
                    messageText
            ).create();

            // Log notification
            Notification notification = new Notification();
            notification.setTicket(ticket);
            notification.setPhoneNumber(toPhoneNumber);
            notification.setMessage(messageText);
            notification.setStatus("SENT");
            notificationRepository.save(notification);

            log.info("SMS sent successfully to {}: {}", toPhoneNumber, message.getSid());

        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", toPhoneNumber, e.getMessage());

            // Log failed notification
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