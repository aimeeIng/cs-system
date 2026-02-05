package com.cs.cs.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;


@Entity
@Data
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticketNumber;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    private int position;

    private Integer windowNumber;

    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        status = TicketStatus.WAITING;
    }

    // getters & setters

    public Ticket() {}

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public TicketStatus getStatus() {
        return this.status;
    }
    public void setWindowNumber(int windowNumber) {
        this.windowNumber = windowNumber;
    }

    public int getWindowNumber() {
        return this.windowNumber;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
