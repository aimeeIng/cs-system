package com.cs.cs.controller;


import com.cs.cs.dto.QueueStats;
import com.cs.cs.model.Service;
import com.cs.cs.model.Window;
import com.cs.cs.repository.ServiceRepository;
import com.cs.cs.repository.WindowRepository;
import com.cs.cs.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/display")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed.origins}")
public class DisplayController {

    private final ServiceRepository serviceRepository;
    private final WindowRepository windowRepository;
    private final QueueService queueService;

    @PostMapping("/init")
    public ResponseEntity<String> initializeDatabase() {
        // Create services
        Service deposit = new Service(null, "DEPOSIT", "D", 3, true);
        Service withdraw = new Service(null, "WITHDRAW", "W", 1, true);
        Service loan = new Service(null, "LOAN", "L", 1, true);

        serviceRepository.saveAll(Arrays.asList(deposit, withdraw, loan));

        // Create windows
        List<Window> windows = new ArrayList<>();

        // DEPOSIT windows (3)
        for (int i = 1; i <= 3; i++) {
            Window w = new Window();
            w.setWindowNumber(i);
            w.setService(deposit);
            w.setStatus("IDLE");
            windows.add(w);
        }

        // WITHDRAW window (1)
        Window withdrawWindow = new Window();
        withdrawWindow.setWindowNumber(1);
        withdrawWindow.setService(withdraw);
        withdrawWindow.setStatus("IDLE");
        windows.add(withdrawWindow);

        // LOAN window (1)
        Window loanWindow = new Window();
        loanWindow.setWindowNumber(1);
        loanWindow.setService(loan);
        loanWindow.setStatus("IDLE");
        windows.add(loanWindow);

        windowRepository.saveAll(windows);

        return ResponseEntity.ok("Database initialized successfully!");
    }
    /**
     * Get all active services
     */
    @GetMapping("/services")
    public ResponseEntity<List<Service>> getServices() {
        return ResponseEntity.ok(serviceRepository.findByIsActiveTrue());
    }

    /**
     * Get all windows
     * GET /api/display/windows
     */
    @GetMapping("/windows")
    public ResponseEntity<List<Window>> getWindows() {
        return ResponseEntity.ok(windowRepository.findAll());
    }

    /**
     * Get queue statistics for a service
     * GET /api/display/stats?serviceId=1
     */
    @GetMapping("/stats")
    public ResponseEntity<QueueStats> getQueueStats(@RequestParam Long serviceId) {
        QueueStats stats = queueService.getQueueStats(serviceId);
        return ResponseEntity.ok(stats);
    }
}