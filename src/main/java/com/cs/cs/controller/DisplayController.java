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

import java.util.List;

@RestController
@RequestMapping("/api/display")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed.origins}")
public class DisplayController {

    private final ServiceRepository serviceRepository;
    private final WindowRepository windowRepository;
    private final QueueService queueService;

    /**
     * Get all active services
     * GET /api/display/services
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