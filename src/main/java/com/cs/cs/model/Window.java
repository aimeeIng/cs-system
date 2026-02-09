package com.cs.cs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "windows")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Window {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "window_number", nullable = false)
    private Integer windowNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(length = 20)
    private String status = "IDLE"; // IDLE, SERVING

    @Column(name = "current_ticket_id")
    private Long currentTicketId;
}